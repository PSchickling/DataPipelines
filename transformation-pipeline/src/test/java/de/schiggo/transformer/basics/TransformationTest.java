package de.schiggo.transformer.basics;

import de.schiggo.transformer.basics.interfaces.Transformable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransformationTest {

    @Test
    void iteration() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = l.iterator();

        Transformation<Integer, Integer> uut = new Transformation<>(iter, i -> i);

        Iterator<Integer> iter2 = l.iterator();
        while (iter2.hasNext()) {
            assertThat(uut.hasNext()).isEqualTo(true);
            assertThat(uut.next()).isEqualTo(iter2.next());
        }
        assertThat(uut.hasNext()).isFalse().isEqualTo(false);
    }

    @Test
    void iteration_withoutCallingHasNext() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = l.iterator();

        Transformation<Integer, Integer> uut = new Transformation<>(iter, i -> i);

        Iterator<Integer> iter2 = l.iterator();
        while (iter2.hasNext()) {
            assertThat(uut.next()).isEqualTo(iter2.next());
        }
    }

    @Test
    void iteration_emptyIterator() {
        List<Integer> l = new ArrayList();
        Iterator<Integer> iter = l.iterator();

        Transformation<Integer, Integer> uut = new Transformation<>(iter, i -> i);

        assertThat(uut.hasNext()).isFalse().isEqualTo(iter.hasNext());
    }

    @Test
    void iteration_NoSuchElementException() {
        List<Integer> l = new ArrayList();
        Iterator<Integer> iter = l.iterator();

        Transformation<Integer, Integer> uut = new Transformation<>(iter, i -> i);

        assertThat(uut.hasNext()).isFalse().isEqualTo(iter.hasNext());
        assertThatThrownBy(uut::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void transform() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = Arrays.asList(1, 2, 3, 4).iterator();

        Transformable<Integer> transformation = new Transformation<>(iter, i -> i).transform(i -> i);

        assertThat(transformation).isOfAnyClassIn(Transformation.class);
    }

    @Test
    void filter() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = Arrays.asList(1, 2, 3, 4).iterator();

        Transformable<Integer> filter = new Transformation<>(iter, i -> i).filter(message -> false);

        assertThat(filter).isOfAnyClassIn(Filter.class);
    }

    @Test
    void sink() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = Arrays.asList(1, 2, 3, 4).iterator();

        BasicSink<Integer> sink = new Transformation<>(iter, i -> i).sink(message -> {
        });
    }
}