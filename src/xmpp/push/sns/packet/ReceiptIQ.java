package xmpp.push.sns.packet;

import android.text.TextUtils;

public class ReceiptIQ extends IQ{
	
	private String packectID;

	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
        buf.append("<message");
        buf.append(" type=\"").append("receipt").append("\"");
        if(!TextUtils.isEmpty(packectID)){
        	buf.append(" id=\"").append(packectID).append("\"");
        }
        buf.append("/>");
		return buf.toString();
	}

	@Override
	public void setPacketID(String id){
		packectID = id;
	}
}
