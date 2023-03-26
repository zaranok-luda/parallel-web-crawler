package com.udacity.webcrawler.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A static utility class that loads a JSON configuration file.
 */
public final class ConfigurationLoader {

  private final Path path;

  /**
   * Create a {@link ConfigurationLoader} that loads configuration from the given {@link Path}.
   */
  public ConfigurationLoader(Path path) {
    this.path = Objects.requireNonNull(path);
  }

  /**
   * Loads configuration from this {@link ConfigurationLoader}'s path
   *
   * @return the loaded {@link CrawlerConfiguration}.
   */
  public CrawlerConfiguration load() {
      try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
        return ConfigurationLoader.read(reader);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
  }

  /**
   * Loads crawler configuration from the given reader.
   *
   * @param reader a Reader pointing to a JSON string that contains crawler configuration.
   * @return a crawler configuration
   */
  public static CrawlerConfiguration read(Reader reader) {
    Objects.requireNonNull(reader);
    CrawlerConfiguration.Builder configBuilder = new CrawlerConfiguration.Builder();
    try (InputStream inputStream = new ByteArrayInputStream(CharStreams.toString(reader).getBytes(StandardCharsets.UTF_8));) {

      CrawlerConfiguration jsonConfig = new ObjectMapper().readValue(inputStream, CrawlerConfiguration.class);
      configBuilder
              .addIgnoredUrls(jsonConfig.getIgnoredUrls().stream().map(e -> e.toString()).toArray(String[]::new))
              .addIgnoredWords(jsonConfig.getIgnoredWords().stream().map(e -> e.toString()).toArray(String[]::new))
              .addStartPages(jsonConfig.getStartPages().toArray(new String[0]))
              .setMaxDepth(jsonConfig.getMaxDepth())
              .setParallelism(jsonConfig.getParallelism())
              .setPopularWordCount(jsonConfig.getPopularWordCount())
              .setImplementationOverride(jsonConfig.getImplementationOverride())
              .setTimeoutSeconds(jsonConfig.getTimeout().toSecondsPart())
              .setResultPath(jsonConfig.getResultPath())
              .setProfileOutputPath(jsonConfig.getProfileOutputPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return configBuilder.build();
  }
}
