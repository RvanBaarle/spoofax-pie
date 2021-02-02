package mb.tiger.statix.cli;

import mb.log.slf4j.SLF4JLoggerFactory;
import mb.pie.runtime.PieBuilderImpl;
import mb.spoofax.cli.DaggerSpoofaxCliComponent;
import mb.spoofax.cli.SpoofaxCli;
import mb.spoofax.cli.SpoofaxCliComponent;
import mb.spoofax.core.platform.LoggerFactoryModule;
import mb.spoofax.core.platform.PlatformPieModule;

public class Main {
    public static void main(String[] args) {
        final SpoofaxCliComponent platformComponent = DaggerSpoofaxCliComponent.builder()
            .loggerFactoryModule(new LoggerFactoryModule(new SLF4JLoggerFactory()))
            .platformPieModule(new PlatformPieModule(PieBuilderImpl::new))
            .build();
        final mb.tiger.statix.spoofax.TigerComponent tigerComponent = mb.tiger.statix.spoofax.DaggerTigerComponent.builder()
            .platformComponent(platformComponent)
            .build();
        final SpoofaxCli cmd = platformComponent.getSpoofaxCmd();
        final int status = cmd.run(args, tigerComponent);
        System.exit(status);
    }
}
