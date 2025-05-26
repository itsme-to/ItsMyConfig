package to.itsme.itsmyconfig.benchmark;

import org.openjdk.jmh.annotations.*;
import to.itsme.itsmyconfig.util.Strings;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class ParsePrefixedMessageBenchmark {

    private final String testMessage = "  &a <bold> $test message §with colors</bold> and more text";

    @Setup(Level.Trial)
    public void setup() {
        Strings.setSymbolPrefix("$");
    }

    @Benchmark
    public Optional<String> benchmarkParsePrefixedMessage() {
        return Strings.parsePrefixedMessage(testMessage);
    }
}
