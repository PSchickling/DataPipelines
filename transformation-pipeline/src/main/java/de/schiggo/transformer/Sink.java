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

package de.schiggo.transformer;

import de.schiggo.transformer.exceptions.ErrorHandler;
import de.schiggo.transformer.exceptions.ExceptionHandler;
import de.schiggo.transformer.exceptions.LastEntryFailureException;
import de.schiggo.transformer.exceptions.StateContext;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;

/**
 * Consumer for an {@link Iterator} or {@link Transformable}. When you call {@link #execute()}, then it iterates
 * through all elements and applies the function (given in the constructor) on it.
 */
@RequiredArgsConstructor
public class Sink<T> {

    private final Iterator<T> source;
    private final ApplySink<T> sink;

    /**
     * Executes the whole pipeline.
     */
    public void execute() {
        try {
            while (source.hasNext()) {
                sink.apply(source.next());
            }
        } catch (LastEntryFailureException e) {
            // This doesn't matter
        }
    }

    /**
     * Adds an exception handler before the sink.
     *
     * @param stateContext     Context which you have to set in the data source. Allows to follow the state of the source.
     * @param exceptionHandler Function, which will be applied on elements, which processing failed. Use this function
     *                         to store failed entries to analyse or retry them later.
     * @param <S>              The type of the elements in the data source.
     * @return Sink with an exception handler on step before in the pipeline.
     */
    public <S> Sink<T> exceptionHandling(StateContext<S> stateContext, ErrorHandler<S> exceptionHandler) {
        return new ExceptionHandler<>(source, exceptionHandler, stateContext, true).sink(sink);
    }
}