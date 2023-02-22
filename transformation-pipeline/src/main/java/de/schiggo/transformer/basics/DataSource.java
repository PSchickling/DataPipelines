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

import de.schiggo.transformer.basics.interfaces.ApplyFilter;
import de.schiggo.transformer.basics.interfaces.ApplySink;
import de.schiggo.transformer.basics.interfaces.ApplyTransformation;
import de.schiggo.transformer.basics.interfaces.Transformable;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;

/**
 * The beginning of a pipeline.
 * <br>
 * Takes an iterator as source. Transformation can be done with {@link #transform(ApplyTransformation)}. Filtering of
 * elements can be done with {@link #filter(ApplyFilter)} and to finish a pipeline use {@link #sink(ApplySink)}.
 *
 * @param <T> Type of the elements.
 */
@RequiredArgsConstructor
public class DataSource<T> implements Transformable<T> {

    private final Iterator<T> source;

    private StateContext<T> stateContext = null;

    @Override
    public <S> Transformation<T, S> transform(ApplyTransformation<T, S> transformer) {
        return new Transformation<>(this, transformer);
    }

    @Override
    public Transformable<T> filter(ApplyFilter<T> filter) {
        return new Filter<>(this, filter);
    }

    @Override
    public BasicSink<T> sink(ApplySink<T> result) {
        return new BasicSink<>(this, result);
    }

    @Override
    public boolean hasNext() {
        return source.hasNext();
    }

    @Override
    public T next() {
        T next = source.next();
        if (stateContext != null) {
            stateContext.setNext(next);
        }
        return next;
    }

    /**
     * A {@link StateContext} is only required, if you want to use an {@link ExceptionHandler ExceptionHandler}).
     *
     * @param stateContext StateContext which will be used in the {@link ExceptionHandler ExceptionHandler}
     *                     of the same pipeline.
     * @return this object itself
     * @see ExceptionHandler
     */
    public DataSource<T> setStateContext(StateContext<T> stateContext) {
        this.stateContext = stateContext;
        return this;
    }
}