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
import de.schiggo.transformer.exceptions.ApplyFilterException;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A {@link Transformable} which filters elements from a source {@link Iterator}.
 *
 * @param <T> Type of the {@link Transformable}/{@link Iterator} elements.
 */
@RequiredArgsConstructor
public class Filter<T> implements Transformable<T> {

    private final Iterator<T> source;
    private final ApplyFilter<T> filter;

    // Temporary element representing the next element. Loaded when calling hasNext().
    private T next = null;

    /**
     * Checks if next element is already loaded and valid.
     *
     * @return true if valid, else false.
     */
    private boolean nextIsValid() {
        return next != null && !filter.check(next);
    }

    @Override
    public boolean hasNext() {
        try {
            while (!nextIsValid() && source.hasNext()) {
                next = source.next();
            }
            return nextIsValid();
        } catch (Exception e) {
            next = null;
            throw new ApplyFilterException("Applying filter failed", e);
        }
    }

    /**
     * Clears the temporary stored next element and then returns it.
     *
     * @return the temporary next element, before set it to null.
     */
    private T clearAndReturnNext() {
        T ret = next;
        next = null;
        return ret;
    }

    @Override
    public T next() {
        if (nextIsValid()) {
            return clearAndReturnNext();
        } else if (hasNext()) {
            return clearAndReturnNext();
        }

        throw new NoSuchElementException();
    }

    @Override
    public <S> Transformation<T, S> transform(ApplyTransformation<T, S> transformer) {
        return new Transformation<>(this, transformer);
    }

    @Override
    public Filter<T> filter(ApplyFilter<T> filter) {
        return new Filter<>(this, filter);
    }

    @Override
    public BasicSink<T> sink(ApplySink<T> result) {
        return new BasicSink<>(this, result);
    }
}