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


import lombok.Getter;
import lombok.Setter;

public class StateContext<T> {

    @Getter
    @Setter
    private T next = null;

    /**
     * Checks if the given object is equal to the current one. Can be used to check, if the
     * {@link de.schiggo.transformer.transformables.DataSource DataSource} element changed (for example after an
     * exception is thrown).
     *
     * @param obj Object for comparison
     * @return True if both are equal, else False.
     */
    public boolean stateEquals(T obj) {
        return (obj == null && next == null) || (obj != null && obj.equals(next));
    }

}
