package de.schiggo.transformer.basics;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SinkTest {

    @Test
    void execute() {
        List<Integer> l = Arrays.asList(1, 2, 3);
        List<Integer> l2 = new ArrayList<>();
        l2.add(1);
        l2.add(2);
        l2.add(3);

        Sink<Integer> uut = new Sink<>(l.iterator(), i -> l2.remove(i));

        uut.execute();

        assertThat(l2).hasSize(0);
    }

    @Test
    void exceptionHandling() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = Arrays.asList(1, 2, 3, 4).iterator();
        StateContext<Integer> sc = new StateContext<>();

        Sink<Integer> uut = new Sink<>(iter, i -> {
        });

        // TODO how to check if ExceptionHandler is between iterator and sink?
    }
}