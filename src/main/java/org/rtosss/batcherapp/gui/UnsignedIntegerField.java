package org.rtosss.batcherapp.gui;

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
        
        /*UnaryOperator<Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            try {
                Integer.parseUnsignedInt(newText);
                return change;
            } catch (NumberFormatException e) {
            }
            return null;
        };

        this.setTextFormatter(
            new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));*/
        
        /*this.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                try {
                	Integer.parseUnsignedInt(newValue);
                } catch (NumberFormatException e) {
                    this.setText(oldValue);
                }
            }
        });*/

	}
	
	/**
	 * this method is called when user inputs text into the textField
	 */
	@Override
	public void replaceText(int start, int end, String text) {
	    if (!text.equals("")) {
	        if (!text.matches("[0-9]")) {
	            return;
	        }
	    }
	    super.replaceText(start, end, text);
	}

	/**
	 * this method is called when user pastes text into the textField
	 */
	@Override
	public void replaceSelection(String text) {
	    if (!text.equals("")) {
	        if (!text.matches("[0-9]+")) {
	            return;
	        }
	    }
	    super.replaceSelection(text);
	}
	
	class IntegerStringFilteredConverter extends IntegerStringConverter {
        public UnaryOperator<TextFormatter.Change> getFilter() {
            return change -> {
                String newText = change.getControlNewText();
                if (newText.isEmpty()) {
                    return change;
                }

                try {
                    Integer.parseUnsignedInt(newText);
                    return change;
                } catch (NumberFormatException e) {
                	return null;
                }
            };
        }
    }
}
