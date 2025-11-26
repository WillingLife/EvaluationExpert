package com.smartcourse.infra.tika;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
public class TikaTextExtractor {

    public String extractText(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new IllegalArgumentException("fileUrl must not be blank");
        }

        try (InputStream inputStream = new BufferedInputStream(new URL(fileUrl).openStream())) {
            Parser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            context.set(Parser.class, parser);
            parser.parse(inputStream, handler, metadata, context);
            return handler.toString();
        } catch (IOException | TikaException | SAXException e) {
            throw new IllegalStateException("Failed to extract document text from url: " + fileUrl, e);
        }
    }
}
