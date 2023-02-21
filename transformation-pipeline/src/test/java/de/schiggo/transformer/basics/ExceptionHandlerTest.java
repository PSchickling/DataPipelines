package de.schiggo.transformer.basics;

import de.schiggo.transformer.exceptions.PipelineFailedException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * This tests not only the component itself. It completely tests, if exception handling is working together with other
 * pipeline components.
 * <br>
 * We expect, that failures can only occur by the methods given to the pipeline and that the pipeline-components itself
 * does not produce errors. Other pipeline components are tested separate.
 */
class ExceptionHandlerTest {

    /**
     * If this fails, then you have problems in other components, which you have to solve first.
     */
    @BeforeAll
    public static void requirements() {
        // Here we ensure, that the simple pipeline components are working
        // If other pipeline components not work, then we cannot test exception handling
        Sink<Integer> sink = new DataSource<>(Arrays.asList("1,2,3", "4,6,7", "8,9,0", "11,4,0").iterator())
                .transform(s -> Integer.valueOf(s.split(",")[0]))
                .filter(i -> i % 2 == 0)
                .sink(e -> {
                });
        sink.execute();
    }

    @Test
    void success() {
        StateContext<String> sc = new StateContext<>();

        Sink<Integer> sink = new DataSource<>(Arrays.asList("1,2,3", "4,6,7", "8,9,0", "11,4,0").iterator())
                .setStateContext(sc)
                .transform(s -> Integer.valueOf(s.split(",")[0]))
                .filter(i -> i % 2 == 0)
                .sink(e -> {
                })
                .exceptionHandling(sc, (i, e) -> {
                    throw new AssertionError("Failure detected, but not expected");
                }, true);

        sink.execute();
    }

    @Test
    void transformFailed_BeforeTransformAndDetected() {
        List<String> failedStrings = new ArrayList<>();

        StateContext<String> sc = new StateContext<>();

        Sink<Integer> sink = new DataSource<>(Arrays.asList("1,2,3", "4,6,7", "8,9,0", "11,4,0").iterator())
                .setStateContext(sc)
                .transform(s -> {
                    if (s.equals("4,6,7")) {
                        throw new RuntimeException("This error should be caught by the ExceptionHandler");
                    }
                    return s;
                })
                .transform(s -> Integer.valueOf(s.split(",")[0]))
                .filter(i -> i % 2 == 1)
                .sink(e -> {
                })
                .exceptionHandling(sc, (i, e) -> failedStrings.add(i), true);

        sink.execute();

        assertThat(failedStrings).hasSize(1);
        assertThat(failedStrings.get(0)).isEqualTo("4,6,7");
    }

    @Test
    void transformFailed_AfterTransformAndDetected() {
        List<String> failedStrings = new ArrayList<>();

        StateContext<String> sc = new StateContext<>();

        Sink<Integer> sink = new DataSource<>(Arrays.asList("1,2,3", "4,6,7", "8,9,0", "11,4,0").iterator())
                .setStateContext(sc)
                .transform(s -> Integer.valueOf(s.split(",")[0]))
                .filter(i -> i % 2 == 1)
                .transform(i -> {
                    if (i.intValue() == 4) {
                        throw new RuntimeException("This error should be caught by the ExceptionHandler");
                    }
                    return i;
                })
                .sink(e -> {
                })
                .exceptionHandling(sc, (i, e) -> failedStrings.add(i), true);

        sink.execute();

        assertThat(failedStrings).hasSize(1);
        assertThat(failedStrings.get(0)).isEqualTo("4,6,7");
    }

    @Test
    void transformFailed_proceedFalse() {
        List<String> failedStrings = new ArrayList<>();

        StateContext<String> sc = new StateContext<>();

        Sink<Integer> sink = new DataSource<>(Arrays.asList("1,2,3", "4,6,7", "8,9,0", "11,4,0").iterator())
                .setStateContext(sc)
                .transform(s -> Integer.valueOf(s.split(",")[0]))
                .filter(i -> i % 2 == 1)
                .transform(i -> {
                    if (i.intValue() == 4 || i.intValue() == 8) {
                        throw new RuntimeException("This error should be caught by the ExceptionHandler");
                    }
                    return i;
                })
                .sink(e -> {
                })
                .exceptionHandling(sc, (i, e) -> failedStrings.add(i), false);


        assertThatThrownBy(() -> {
            sink.execute();
        }).isInstanceOf(PipelineFailedException.class);

        assertThat(failedStrings).hasSize(1);
        assertThat(failedStrings.get(0)).isEqualTo("4,6,7");
    }

    @Test
    void filterFailed_BeforeTransformAndDetected() {
        List<String> failedStrings = new ArrayList<>();

        StateContext<String> sc = new StateContext<>();

        Sink<Integer> sink = new DataSource<>(Arrays.asList("1,2,3", "4,6,7", "8,9,0", "11,4,0").iterator())
                .setStateContext(sc)
                .filter(s -> {
                    if (s.equals("4,6,7")) {
                        throw new RuntimeException("This error should be caught by the ExceptionHandler");
                    }
                    return false;
                })
                .transform(s -> Integer.valueOf(s.split(",")[0]))
                .filter(i -> i % 2 == 1)
                .sink(e -> {
                })
                .exceptionHandling(sc, (i, e) -> failedStrings.add(i), true);

        sink.execute();

        assertThat(failedStrings).hasSize(1);
        assertThat(failedStrings.get(0)).isEqualTo("4,6,7");
    }

    @Test
    void filterFailed_AfterTransformAndDetected() {
        List<String> failedStrings = new ArrayList<>();

        StateContext<String> sc = new StateContext<>();

        Sink<Integer> sink = new DataSource<>(Arrays.asList("1,2,3", "4,6,7", "8,9,0", "11,4,0").iterator())
                .setStateContext(sc)
                .transform(s -> Integer.valueOf(s.split(",")[0]))
                .filter(i -> i % 2 == 1)
                .filter(i -> {
                    if (i.intValue() == 4) {
                        throw new RuntimeException("This error should be caught by the ExceptionHandler");
                    }
                    return false;
                })
                .sink(e -> {
                })
                .exceptionHandling(sc, (i, e) -> failedStrings.add(i), true);

        sink.execute();

        assertThat(failedStrings).hasSize(1);
        assertThat(failedStrings.get(0)).isEqualTo("4,6,7");
    }

    @Test
    void filterFailed_proceedFalse() {
        List<String> failedStrings = new ArrayList<>();

        StateContext<String> sc = new StateContext<>();

        Sink<Integer> sink = new DataSource<>(Arrays.asList("1,2,3", "4,6,7", "8,9,0", "11,4,0").iterator())
                .setStateContext(sc)
                .transform(s -> Integer.valueOf(s.split(",")[0]))
                .filter(i -> i % 2 == 1)
                .filter(i -> {
                    if (i.intValue() == 4 || i.intValue() == 8) {
                        throw new RuntimeException("This error should be caught by the ExceptionHandler");
                    }
                    return false;
                })
                .sink(e -> {
                })
                .exceptionHandling(sc, (i, e) -> failedStrings.add(i), false);


        assertThatThrownBy(() -> {
            sink.execute();
        }).isInstanceOf(PipelineFailedException.class);

        assertThat(failedStrings).hasSize(1);
        assertThat(failedStrings.get(0)).isEqualTo("4,6,7");
    }

    @Test
    void lastEntryFailedFilter() {
        List<String> failedStrings = new ArrayList<>();

        StateContext<String> sc = new StateContext<>();

        Sink<Integer> sink = new DataSource<>(Arrays.asList("1,2,3", "4,6,7", "8,9,0", "11,4,0").iterator())
                .setStateContext(sc)
                .transform(s -> Integer.valueOf(s.split(",")[0]))
                .filter(i -> i % 2 == 0)
                .filter(i -> {
                    if (i.intValue() == 11) {
                        throw new RuntimeException("This error should be caught by the ExceptionHandler");
                    }
                    return false;
                })
                .sink(e -> {
                })
                .exceptionHandling(sc, (i, e) -> failedStrings.add(i), true);

        sink.execute();

        assertThat(failedStrings).hasSize(1);
        assertThat(failedStrings.get(0)).isEqualTo("11,4,0");
    }

    @Test
    void lastEntryFailedTransform() {
        List<String> failedStrings = new ArrayList<>();

        StateContext<String> sc = new StateContext<>();

        Sink<Integer> sink = new DataSource<>(Arrays.asList("1,2,3", "4,6,7", "8,9,0", "11,4,0").iterator())
                .setStateContext(sc)
                .transform(s -> Integer.valueOf(s.split(",")[0]))
                .filter(i -> i % 2 == 0)
                .transform(i -> {
                    if (i.intValue() == 11) {
                        throw new RuntimeException("This error should be caught by the ExceptionHandler");
                    }
                    return i;
                })
                .sink(e -> {
                })
                .exceptionHandling(sc, (i, e) -> failedStrings.add(i), true);

        sink.execute();

        assertThat(failedStrings).hasSize(1);
        assertThat(failedStrings.get(0)).isEqualTo("11,4,0");
    }

    /**
     * TODO currently not supported
     */
    @Disabled
    @Test
    void sinkFailed() {
        List<String> failedStrings = new ArrayList<>();

        StateContext<String> sc = new StateContext<>();

        Sink<Integer> sink = new DataSource<>(Arrays.asList("1,2,3", "4,6,7", "8,9,0", "11,4,0").iterator())
                .setStateContext(sc)
                .transform(s -> Integer.valueOf(s.split(",")[0]))
                .filter(i -> i % 2 == 1)
                .sink(i -> {
                    if (i.intValue() == 4) {
                        throw new RuntimeException("This error should be caught by the ExceptionHandler");
                    }
                })
                .exceptionHandling(sc, (i, e) -> failedStrings.add(i), true);

        sink.execute();

        assertThat(failedStrings).hasSize(1);
        assertThat(failedStrings.get(0)).isEqualTo("4,6,7");
    }

    @Test
    void iteration() {
        StateContext<String> sc = new StateContext<>();
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Iterator<Integer> iter = Arrays.asList(1, 2, 3, 4).iterator();
        ExceptionHandler uut = new ExceptionHandler<>(iter, (input, e) -> {
        }, sc, true);

        int i = 0;
        while (iter.hasNext()) {
            assertThat(uut.hasNext()).isEqualTo(iter.hasNext());
            assertThat(uut.next()).isEqualTo(l.get(i));
            i++;
        }
        assertThat(uut.hasNext()).isFalse().isEqualTo(iter.hasNext());
    }

    @Test
    void iteration_emptyIterator() {
        StateContext<String> sc = new StateContext<>();
        List<Integer> l = new ArrayList();
        Iterator<Integer> iter = l.iterator();
        ExceptionHandler uut = new ExceptionHandler<>(iter, (input, e) -> {
        }, sc, true);

        assertThat(uut.hasNext()).isFalse().isEqualTo(iter.hasNext());
    }

    @Test
    void iteration_NoSuchElementException() {
        StateContext<String> sc = new StateContext<>();
        List<Integer> l = new ArrayList();
        Iterator<Integer> iter = l.iterator();

        ExceptionHandler uut = new ExceptionHandler<>(iter, (input, e) -> {
        }, sc, true);

        assertThat(uut.hasNext()).isFalse().isEqualTo(iter.hasNext());
        assertThatThrownBy(() -> {
            uut.next();
        }).isInstanceOf(NoSuchElementException.class);
    }

}