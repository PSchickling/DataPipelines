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
 * Represents a type transformation for elements of a {@link Iterator} or a {@link Transformable} into another type.
 *
 * @param <T> Source type
 * @param <S> Target type
 */
@RequiredArgsConstructor
public class Transformation<T, S> implements Transformable<S> {
    private final Iterator<T> source;
    private final ApplyTransformation<T, S> transformer;

    @Override
    public <U> Transformation<S, U> transform(ApplyTransformation<S, U> transformer) {
        return new Transformation<>(this, transformer);
    }

    @Override
    public Transformable<S> filter(ApplyFilter<S> filter) {
        return new Filter<>(this, filter);
    }

    @Override
    public Sink<S> sink(ApplySink<S> result) {
        return new Sink<>(this, result);
    }

    @Override
    public boolean hasNext() {
        return source.hasNext();
    }

    @Override
    public S next() {
        return transformer.apply(source.next());
    }
}