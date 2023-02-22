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
import de.schiggo.transformer.basics.interfaces.Transformable;
import de.schiggo.transformer.exceptions.PipelineFailedException;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;

/**
 * Consumer for an {@link Iterator} or {@link Transformable}. When you call {@link #execute()}, then it iterates
 * through all elements and applies the function (given in the constructor) on it.
 */
@RequiredArgsConstructor
public class BasicSink<T> implements Sink<T> {

    private final Iterator<T> source;
    private final ApplySink<T> sink;


    @Override
    public void execute() {
        try {
            while (source.hasNext()) {
                sink.apply(source.next());
            }
        } catch (Exception e) {
            throw new PipelineFailedException("Failure at pipeline execution", e);
        }
    }

    @Override
    public <S> Sink<T> exceptionHandling(StateContext<S> stateContext, ErrorHandler<S> exceptionHandler, boolean proceedOnFailure) {
        return new ExceptionHandler<>(sink, source, exceptionHandler, stateContext, proceedOnFailure);
    }
}