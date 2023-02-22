package de.schiggo.transformer.basics;

import de.schiggo.transformer.basics.interfaces.Sink;
import de.schiggo.transformer.exceptions.PipelineFailedException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SinkTest {

    @Test
    void execute() {
        List<Integer> l = Arrays.asList(1, 2, 3);
        List<Integer> l2 = new ArrayList<>();
        l2.add(1);
        l2.add(2);
        l2.add(3);

        BasicSink<Integer> uut = new BasicSink<>(l.iterator(), l2::remove);

        uut.execute();

        assertThat(l2).hasSize(0);
    }

    @Test
    void exceptionHandling() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = l.iterator();
        StateContext<Integer> sc = new StateContext<>();

        Sink<Integer> sink = new BasicSink<>(iter, i -> {
        }).exceptionHandling(sc, (input, e) -> {
        }, true);

        assertThat(sink).isOfAnyClassIn(ExceptionHandler.class);
    }

    @Test
    void applySinkFailure() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = l.iterator();

        BasicSink<Integer> uut = new BasicSink<>(iter, i -> {
            throw new RuntimeException();
        });

        assertThatThrownBy(uut::execute).isInstanceOf(PipelineFailedException.class);
    }

    @Test
    void pipelineFailure() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = l.iterator();

        BasicSink<Integer> uut = new BasicSink<>(new Iterator<>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Integer next() {
                throw new RuntimeException();
            }
        }, i -> {
        });

        assertThatThrownBy(uut::execute).isInstanceOf(PipelineFailedException.class);
    }
}