/**
 * $RCSfile$
 * $Revision: 2779 $
 * $Date: 2005-09-05 17:00:45 -0300 (Mon, 05 Sep 2005) $
 *
 * Copyright 2003-2006 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xmpp.push.sns.muc;

import xmpp.push.sns.PacketListener;
import xmpp.push.sns.filter.MessageTypeFilter;
import xmpp.push.sns.filter.PacketExtensionFilter;
import xmpp.push.sns.filter.PacketFilter;
import xmpp.push.sns.filter.PacketTypeFilter;
import xmpp.push.sns.packet.Message;
import xmpp.push.sns.packet.Packet;
import xmpp.push.sns.packet.Presence;

/**
 * The single <code>PacketListener</code> used by each {@link MultiUserChat}
 * for all basic processing of presence, and message packets targeted to that chat.
 *
 * @author Larry Kirschner
 */
class PacketMultiplexListener implements PacketListener {

    private static final PacketFilter MESSAGE_FILTER =
            new MessageTypeFilter(Message.Type.groupchat);
    private static final PacketFilter PRESENCE_FILTER = new PacketTypeFilter(Presence.class);
    private static final PacketFilter SUBJECT_FILTER = new PacketFilter() {
        @Override
		public boolean accept(Packet packet) {
            Message msg = (Message) packet;
            return msg.getSubject() != null;
        }
    };
    private static final PacketFilter DECLINES_FILTER =
            new PacketExtensionFilter("x",
                    "http://jabber.org/protocol/muc#user");

    private ConnectionDetachedPacketCollector messageCollector;
    private PacketListener presenceListener;
    private PacketListener subjectListener;
    private PacketListener declinesListener;

    public PacketMultiplexListener(
            ConnectionDetachedPacketCollector messageCollector,
            PacketListener presenceListener,
            PacketListener subjectListener, PacketListener declinesListener) {
        if (messageCollector == null) {
            throw new IllegalArgumentException("MessageCollector is null");
        }
        if (presenceListener == null) {
            throw new IllegalArgumentException("Presence listener is null");
        }
        if (subjectListener == null) {
            throw new IllegalArgumentException("Subject listener is null");
        }
        if (declinesListener == null) {
            throw new IllegalArgumentException("Declines listener is null");
        }
        this.messageCollector = messageCollector;
        this.presenceListener = presenceListener;
        this.subjectListener = subjectListener;
        this.declinesListener = declinesListener;
    }

    @Override
	public void processPacket(Packet p) {
    	Message message = (Message)p;
        if (PRESENCE_FILTER.accept(p)) {
            presenceListener.processPacket(p);
        }
        else if (MESSAGE_FILTER.accept(p)) {
            messageCollector.processPacket(p);

            if (SUBJECT_FILTER.accept(p)) {
                subjectListener.processPacket(p);
            }
        }
        else if (DECLINES_FILTER.accept(p)) {
            declinesListener.processPacket(p);
        }
    }

}
