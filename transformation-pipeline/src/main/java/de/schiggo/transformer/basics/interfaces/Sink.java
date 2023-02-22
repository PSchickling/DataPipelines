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

package de.schiggo.transformer.basics.interfaces;

import de.schiggo.transformer.basics.StateContext;

public interface Sink<T> {

    /**
     * Executes the whole pipeline.
     */
    void execute();

    /**
     * Adds an exception handler before the sink.
     *
     * @param stateContext     Context which you have to set in the data source. Allows to follow the state of the source.
     * @param exceptionHandler Function, which will be applied on elements, which processing failed. Use this function
     *                         to store failed entries to analyse or retry them later.
     * @param proceedOnFailure if true, then the pipeline proceed when a failure occured, ese the pipeline throws a
     *                         {@link de.schiggo.transformer.exceptions.PipelineFailedException PipelineFailedException}
     * @param <S>              The type of the elements in the data source.
     * @return BasicSink with an exception handler on step before in the pipeline.
     */
    <S> Sink<T> exceptionHandling(StateContext<S> stateContext, ErrorHandler<S> exceptionHandler, boolean proceedOnFailure);

}
