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
import org.example.utils.UrlUtils;


import java.net.InetSocketAddress;
import java.net.URI;

@Slf4j
public class MasterNode {

	public static void main(String[] args) {
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		options.addOption(
				Option.builder("m")
						.longOpt("master")
						.desc("Master node address")
						.hasArg()
						.argName("MASTER")
						.build());

		options.addOption(
				Option.builder("s")
						.longOpt("second")
						.desc("Secondaries nodes addresses")
						.hasArg()
						.argName("SECOND")
						.build());

		boolean printHelp = false;
		try {
			CommandLine line = parser.parse(options, args);
			URI master = UrlUtils.getUri(line.getOptionValue("master"));
			if (master == null) {
				log.error("Invalid master url");
				printHelp = true;
				return;
			}

			String[] secondaries = line.getOptionValues("second");
			if (secondaries == null) {
				log.error("No secondaries urls");
				secondaries = new String[]{};
			}

			HttpServer server = HttpServer.create(new InetSocketAddress(master.getPort()), 0);
			server.createContext("/", new RootHandler(secondaries));
			server.start();
			log.info("Master node started at host:{} port:{}, secondaries: {}", master.getHost(), master.getPort(), secondaries);

		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			if (printHelp) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("-m <MASTER> -s <SECOND>", options);
			}
		}
	}
}
