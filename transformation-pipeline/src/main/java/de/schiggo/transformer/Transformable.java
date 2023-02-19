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

import java.util.Iterator;

/**
 * {@link Transformable}s are {@link Iterator}s, which provide additional functions like type transformation or filtering.
 * entries.
 *
 * @param <T> Type of the Transformable. Can be converted to another type using {@link #transform(ApplyTransformation)}.
 */
public interface Transformable<T> extends Iterator<T> {

    /**
     * Performs a type conversation. In general, conversion will only be performed when {@link #next()} is called.
     *
     * @param transformer Function transforming each entry into another type.
     * @param <S>         The type after the transformation.
     * @return The transformable with the new type.
     */
    <S> Transformable<S> transform(ApplyTransformation<T, S> transformer);

    /**
     * Apply a filter to the transformable to exclude specific elements.
     *
     * @param filter Function which consumes an elements from the iterator and returns true, if the element should be
     *               excluded.
     * @return The new transformable, where the elements are filtered by the given function.
     */
    Transformable<T> filter(ApplyFilter<T> filter);

    /**
     * Create a {@link Sink}, that can consume the elements of the transformable with the given function.
     *
     * @param result The function of the sink, which consumes each element.
     * @return The sink
     */
    Sink<T> sink(ApplySink<T> result);

}
