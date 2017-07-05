package net.idea.opentox.main;

import com.fasterxml.jackson.databind.JsonNode;

public interface IJSONCallBack {
	void callback(JsonNode node);
}
