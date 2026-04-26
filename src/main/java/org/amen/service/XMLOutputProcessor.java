package org.amen.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.amen.domain.Sentence;
import org.amen.domain.Text;

import java.io.StringWriter;

public class XMLOutputProcessor implements IOutputProcessor{
    @Override
    public String writeToOutput(Text text) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Text.class, Sentence.class);
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

        StringWriter writer = new StringWriter();
        marshaller.marshal(text, writer);

        return formatXmlWithSentenceLineBreaks(writer.toString());
    }

    private String formatXmlWithSentenceLineBreaks(String xml) {
        return xml
                .replace("?><text>", "?>\n<text>\n")
                .replace("<text><sentence>", "<text>\n<sentence>")
                .replace("</sentence><sentence>", "</sentence>\n<sentence>")
                .replace("</sentence></text>", "</sentence>\n</text>");
    }
}
