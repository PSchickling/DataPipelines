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

    public void execute() {
        while (source.hasNext()) {
            sink.apply(source.next());
        }
    }
}