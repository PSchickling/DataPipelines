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

package de.schiggo.transformer.exceptions;

/**
 * Definition of functions for exception handling of {@link ExceptionHandler}.
 *
 * @param <T> Usually, the {@link de.schiggo.transformer.transformables.DataSource DataSource}-type of the pipeline.
 */
public interface ErrorHandler<T> {
    /**
     * Method to handle exceptions in a pipeline.
     * <br>
     * For example, you can use this method to store the failed element to retry or analyse it.
     *
     * @param input The inital element from the {@link de.schiggo.transformer.transformables.DataSource DataSource}
     *              which failed.
     * @param e     The exception which was thrown in the pipeline.
     */
    void handle(T input, Exception e);
}