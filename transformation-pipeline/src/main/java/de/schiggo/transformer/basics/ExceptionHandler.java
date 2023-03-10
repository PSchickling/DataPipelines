/*
 * Copyright 2023 Paul Schickling
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.schiggo.transformer.basics;

import de.schiggo.transformer.basics.interfaces.ApplySink;
import de.schiggo.transformer.basics.interfaces.ErrorHandler;
import de.schiggo.transformer.basics.interfaces.Sink;
import de.schiggo.transformer.exceptions.LastEntryFailureException;
import de.schiggo.transformer.exceptions.PipelineFailedException;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Exception handler for pipelines.
 * <br>
 * Example:
 * <pre>{@code
 *      StateContext<Long> sc = new StateContext<>();
 *      BasicSink<PersonReportingEntity> sink = new DataSource<>(...)
 *          .setStateContext(sc)
 *          .transform(...)
 *          .sink(...)
 *          .exceptionHandling(sc, (i, e) -> System.out.println("Failed to process one element"));
 *      sink.execute();
 * }</pre>
 *
 * @param <T> BasicSink type
 * @param <S> DataSource type
 */
@RequiredArgsConstructor
public class ExceptionHandler<T, S> implements Iterator<T>, Sink<T> {


    private final ApplySink<T> sink;

    private final Iterator<T> source;

    private final ErrorHandler<S> errorHandler;

    private final StateContext<S> stateContext;

    private final boolean proceedOnFailure;

    private S previous = null;

    @Override
    public boolean hasNext() {

        try {
            return source.hasNext();
        } catch (Exception e) {
            errorHandler.handle(stateContext.getNext(), e);
            if (!proceedOnFailure || stateContext.stateEquals(previous)) {
                throw new PipelineFailedException("Cannot proceed iteration after hasNext()-failure", e);
            }
        }

        return hasNext();
    }

    @Override
    public T next() {

        try {
            // Try to get next
            T next = source.next();
            // Store next for next time
            previous = stateContext.getNext();
            return next;
        } catch (LastEntryFailureException e) {
            throw e;
        } catch (NoSuchElementException e) {
            // No such element exceptions can be thrown, because it relates to the iterator interface.
            // Should not be thrown, when the user ensures that hasNext() is true before calling next().
            throw e;
        } catch (PipelineFailedException e) {
            throw e;
        } catch (Exception e) {
            // Apply exception handling function
            errorHandler.handle(stateContext.getNext(), e);

            // If proceeding with next entry is not possible, then stop
            if (!proceedOnFailure || stateContext.stateEquals(previous)) {
                throw new PipelineFailedException("Cannot proceed iteration after next()-failure", e);
            }
        }

        // Proceed with next pipeline element after failure
        if (hasNext()) {
            return next();
        } else {
            // Cannot proceed because last element failed
            throw new LastEntryFailureException();
        }
    }

    @Override
    public void execute() {
        try {
            while (hasNext()) {
                sink.apply(next());
            }
            // stop execution immediately
            return;
        } catch (LastEntryFailureException e) {
            // This doesn't matter
        } catch (PipelineFailedException e) {
            throw e;
        } catch (Exception e) {
            // Apply exception handling function
            errorHandler.handle(stateContext.getNext(), e);

            // If proceeding with next entry is not possible, then stop
            if (!proceedOnFailure) {
                throw new PipelineFailedException("Cannot proceed iteration after next()-failure", e);
            }
        }

        // Execution was disturbed, proceed with next pipeline element after failure
        execute();
    }

    @Override
    public <S> Sink<T> exceptionHandling(StateContext<S> stateContext, ErrorHandler<S> exceptionHandler, boolean proceedOnFailure) {
        // For case an exception occurs while handling an exception LOL
        return new ExceptionHandler<>(sink, this, exceptionHandler, stateContext, proceedOnFailure);
    }

}