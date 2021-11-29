package org.example;

import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.example.handler.RootHandler;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;

@Slf4j
public class SecondaryNode {

	public static void main(String[] args) {
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		options.addOption(
				Option.builder("h")
						.longOpt("host")
						.desc("Host address")
						.hasArg()
						.argName("HOST")
						.build());

		options.addOption(
				Option.builder("p")
						.longOpt("port")
						.desc("Port")
						.hasArg()
						.argName("PORT")
						.build());

		options.addOption(
				Option.builder("d")
						.longOpt("delay")
						.desc("Delay")
						.hasArg()
						.argName("DELAY")
						.build());

		boolean printHelp = false;
		try {
			CommandLine line = parser.parse(options, args);
			var host = line.getOptionValue("h");
			if (host == null) {
				log.warn("Host not set.");
				host = InetAddress.getLocalHost().getHostName();
				log.warn("Default host will be used: {}", host);
			}

			String port = line.getOptionValue("p");
			if (port == null) {
				log.error("Port not set");
				printHelp = true;
				return;
			}

			var delay = line.getOptionValue("d");
			if (delay == null) {
				log.info("Delay not set");
				delay = "0";
			}

			HttpServer server = HttpServer.create(new InetSocketAddress(host, Integer.parseInt(port)), 0);
			server.createContext("/", new RootHandler(Long.parseLong(delay)));
			server.start();
			log.info("Secondary node started with delay {} on host:{}, port {}", delay, host, port);
		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			if (printHelp) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("-h <HOST> -p <PORT> -d <DELAY>", options);
			}
		}
	}
}
