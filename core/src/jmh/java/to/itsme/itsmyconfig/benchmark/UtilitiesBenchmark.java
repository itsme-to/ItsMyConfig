package to.itsme.itsmyconfig.benchmark;

import org.openjdk.jmh.annotations.*;
import to.itsme.itsmyconfig.util.Strings;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class UtilitiesBenchmark {

    @Param({
        "&aHello $world", 
        "<bold><italic>$PrefixText</italic></bold>", 
        "   §a<hover:show_text:'&bClick'>$Click me!" 
    })
    public String prefixedMessage;

    @Param({
        "This is a <quote:ignorecolors>This <red>shouldn't</red> be colored</quote> test.", 
        "<quote>This is <italic>quoted</italic></quote>", 
        "No quotes here, just a normal string." 
    })
    public String quoteText;

    @Param({
        "§aGreen §bBlue §cRed", 
        "&aGreen &bBlue &cRed", 
        "No formatting here." 
    })
    public String coloredText;

    @Setup(Level.Trial)
    public void setup() {
        Strings.setSymbolPrefix("$");
    }

    @Benchmark
    public Optional<String> benchmarkParsePrefixedMessage() {
        return Strings.parsePrefixedMessage(prefixedMessage);
    }

    @Benchmark
    public String benchmarkQuote() {
        return Strings.quote(quoteText);
    }

    @Benchmark
    public String benchmarkColorless() {
        return Strings.colorless(coloredText);
    }
}
