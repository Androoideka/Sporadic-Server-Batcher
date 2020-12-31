package org.rtosss.batcherapp.gui;

import org.rtosss.batcherapp.model.RTS;

public interface IStatusObserver {
	void updateStatus(Status status);
	void setRTS(RTS system);
}
