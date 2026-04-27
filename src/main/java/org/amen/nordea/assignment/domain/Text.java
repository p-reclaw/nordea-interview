package org.amen.nordea.assignment.domain;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "text")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor // JAXB
public class Text {
    @XmlElement(name = "sentence")
    private List<Sentence> sentences = new ArrayList<>();

    public Text(List<Sentence> sentences) {
        this.sentences = sentences;
        recalculateMaxLength();
    }

    @XmlTransient
    private int maxLength;

    public void addSentence(Sentence sentence) {
        if (sentence == null) {
            return;
        }

        if (sentences == null) {
            sentences = new ArrayList<>();
        }

        sentences.add(sentence);

        int sentenceLength = getSentenceLength(sentence);
        maxLength = Math.max(maxLength, sentenceLength);
    }

    public boolean removeSentence(Sentence sentence) {
        if (sentences == null || sentence == null) {
            return false;
        }

        boolean removed = sentences.remove(sentence);

        if (removed) {
            recalculateMaxLength();
        }

        return removed;
    }

    public Sentence removeSentence(int index) {
        if (sentences == null) {
            throw new IndexOutOfBoundsException("No sentences available");
        }

        Sentence removed = sentences.remove(index);
        recalculateMaxLength();

        return removed;
    }

    private void recalculateMaxLength() {
        if (sentences == null) {
            maxLength = 0;
            return;
        }

        maxLength = sentences.stream()
                .mapToInt(this::getSentenceLength)
                .max()
                .orElse(0);
    }

    private int getSentenceLength(Sentence sentence) {
        if (sentence == null || sentence.getWords() == null) {
            return 0;
        }

        return sentence.getWords().size();
    }
}
