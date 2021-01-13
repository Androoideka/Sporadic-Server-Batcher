package org.rtosss.batcherapp.gui.components;

import java.util.function.UnaryOperator;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

public class UnsignedIntegerField extends TextField {
	
	public UnsignedIntegerField() {
		super();
		IntegerStringFilteredConverter converter = new IntegerStringFilteredConverter();
		final TextFormatter<Integer> formatter = new TextFormatter<>(
                converter,
                0,
                converter.getFilter()
        );

        this.setTextFormatter(formatter);
	}
	
	@Override
	public void replaceText(int start, int end, String text) {
	    if (!text.equals("")) {
	        if (!text.matches("[0-9]")) {
	            return;
	        }
	    }
	    super.replaceText(start, end, text);
	}
	
	class IntegerStringFilteredConverter extends IntegerStringConverter {
        public UnaryOperator<TextFormatter.Change> getFilter() {
            return change -> {
                String newText = change.getControlNewText();
                if (newText.isEmpty()) {
                    return null;
                }

                try {
                    Integer.parseUnsignedInt(newText);
                    return change;
                } catch (NumberFormatException e) {
                }
            	return null;
            };
        }
    }
}
