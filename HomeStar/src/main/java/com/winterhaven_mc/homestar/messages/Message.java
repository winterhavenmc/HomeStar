package com.winterhaven_mc.homestar.messages;

import com.winterhaven_mc.util.AbstractMessage;
import org.bukkit.command.CommandSender;


public class Message extends AbstractMessage<MessageId,Macro> {


	/**
	 * Class constructor
	 *
	 * @param recipient message recipient
	 * @param messageId message identifier
	 */
	private Message(CommandSender recipient, MessageId messageId) {
		super(recipient, messageId);
	}


	public static Message create(CommandSender recipient, MessageId messageId) {
		return new Message(recipient, messageId);
	}

}
