package org.rtosss.batcherapp.gui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.animation.AnimationTimer;
import javafx.scene.control.TextArea;

public class TextAreaMessageConsumer extends AnimationTimer {
	private final BlockingQueue<Character> messageQueue;
	private final TextArea textArea;
	
	public TextAreaMessageConsumer(TextArea textArea) {
		super();
		messageQueue = new LinkedBlockingQueue<>();
		this.textArea = textArea;
	}

	public BlockingQueue<Character> getMessageQueue() {
		return messageQueue;
	}

	@Override
	public void handle(long arg0) {
		List<Character> chars = new ArrayList<>();
		messageQueue.drainTo(chars);
		textArea.appendText(getStringRepresentation(chars));
	}
	
	private String getStringRepresentation(List<Character> chars)
	{    
	    StringBuilder builder = new StringBuilder(chars.size());
	    for(Character c : chars)
	    {
	        builder.append(c);
	    }
	    return builder.toString();
	}

}
