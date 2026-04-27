package org.amen.nordea.assignment.service.formatter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.amen.nordea.assignment.domain.Sentence;
import org.amen.nordea.assignment.domain.Text;

import java.io.StringWriter;

public class XMLFormatter implements IFormatter {
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
