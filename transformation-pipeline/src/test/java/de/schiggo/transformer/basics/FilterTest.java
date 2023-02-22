package de.schiggo.transformer.basics;

import de.schiggo.transformer.basics.interfaces.Transformable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FilterTest {

    @Test
    void iteration() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = l.iterator();

        Filter<Integer> uut = new Filter<>(iter, input -> false);

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

        Filter<Integer> uut = new Filter<>(iter, input -> false);

        Iterator<Integer> iter2 = l.iterator();
        while (iter2.hasNext()) {
            assertThat(uut.next()).isEqualTo(iter2.next());
        }
    }

    @Test
    void iteration_emptyIterator() {
        List<Integer> l = new ArrayList();
        Iterator<Integer> iter = l.iterator();

        Filter<Integer> uut = new Filter<>(iter, input -> false);

        assertThat(uut.hasNext()).isFalse().isEqualTo(iter.hasNext());
    }

    @Test
    void iteration_NoSuchElementException() {
        List<Integer> l = new ArrayList();
        Iterator<Integer> iter = l.iterator();

        Filter<Integer> uut = new Filter<>(iter, input -> false);

        assertThat(uut.hasNext()).isFalse().isEqualTo(iter.hasNext());
        assertThatThrownBy(uut::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void transform() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = Arrays.asList(1, 2, 3, 4).iterator();

        Transformable<Integer> transformation = new Filter<>(iter, input -> false).transform(i -> i);

        assertThat(transformation).isOfAnyClassIn(Transformation.class);
    }

    @Test
    void filter() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = Arrays.asList(1, 2, 3, 4).iterator();

        Transformable<Integer> filter = new Filter<>(iter, input -> false).filter(message -> false);

        assertThat(filter).isOfAnyClassIn(Filter.class);
    }

    @Test
    void sink() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = Arrays.asList(1, 2, 3, 4).iterator();

        BasicSink<Integer> sink = new Filter<>(iter, input -> false).sink(message -> {
        });
    }
}