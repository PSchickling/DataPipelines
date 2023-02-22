package de.schiggo.transformer.basics;

import de.schiggo.transformer.basics.interfaces.Transformable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataSourceTest {

    @Test
    void transform() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = Arrays.asList(1, 2, 3, 4).iterator();

        Transformable<Integer> transformation = new DataSource<>(iter).transform(i -> i);

        assertThat(transformation).isOfAnyClassIn(Transformation.class);
    }

    @Test
    void filter() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = Arrays.asList(1, 2, 3, 4).iterator();

        Transformable<Integer> filter = new DataSource<>(iter).filter(message -> false);

        assertThat(filter).isOfAnyClassIn(Filter.class);
    }

    @Test
    void sink() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = Arrays.asList(1, 2, 3, 4).iterator();

        BasicSink<Integer> sink = new DataSource<>(iter).sink(message -> {
        });
    }

    @Test
    void iteration() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = Arrays.asList(1, 2, 3, 4).iterator();

        DataSource<Integer> uut = new DataSource<>(iter);

        int i = 0;
        while (iter.hasNext()) {
            assertThat(uut.hasNext()).isEqualTo(true);
            assertThat(uut.next()).isEqualTo(l.get(i));
            i++;
        }
        assertThat(uut.hasNext()).isFalse().isEqualTo(false);
    }

    @Test
    void iteration_emptyIterator() {
        List<Integer> l = new ArrayList();
        Iterator<Integer> iter = l.iterator();

        DataSource<Integer> uut = new DataSource<>(iter);

        assertThat(uut.hasNext()).isFalse().isEqualTo(iter.hasNext());
    }

    @Test
    void iteration_NoSuchElementException() {
        List<Integer> l = new ArrayList();
        Iterator<Integer> iter = l.iterator();

        DataSource<Integer> uut = new DataSource<>(iter);

        assertThat(uut.hasNext()).isFalse().isEqualTo(iter.hasNext());
        assertThatThrownBy(uut::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void setStateContext() {
        StateContext<Integer> sc = new StateContext<>();
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = Arrays.asList(1, 2, 3, 4).iterator();

        DataSource<Integer> uut = new DataSource<>(iter).setStateContext(sc);

        while (iter.hasNext()) {
            uut.next();
            assertThat(uut.next()).isEqualTo(sc.getNext());
        }
    }
}