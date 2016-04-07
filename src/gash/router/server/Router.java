/**
 * 
 */
package gash.router.server;

import pipe.common.Common.Header;
import pipe.work.Work.WorkMessage;

/**
 * @author saurabh
 *
 */
public class Router {

	private final ServerState state;

	public Router(ServerState state)    {
		this.state = state;
	}

	public WorkMessage route(WorkMessage msg) {
		
		if (msg.getHeader().getNodeId() == state.getConf().getNodeId()) {
			System.out.println("Same message received by source! Dropping message...");
			return null;
		}

		if (msg.getHeader().getDestination() != state.getConf().getNodeId()) {

			if (msg.getHeader().getDestination() == -1) {
				if (msg.getHeader().getMaxHops() > 0) {
						WorkMessage.Builder wb = WorkMessage.newBuilder(msg);
						Header.Builder hb = Header.newBuilder(msg.getHeader());
						hb.setDestination(state.getLeaderId());
						hb.setNodeId(state.getConf().getNodeId());
						wb.setHeader(hb);
						msg = wb.build();
					}
					broadcast(msg);
				} else {
					System.out.println("MAX HOPS is Zro! Dropping message...");
					return null;
				}
			} else {
				if (msg.getHeader().getMaxHops() > 0) {
					broadcast(msg);
					return null;
				} else {
					System.out.println("MAX HOPS is Zero! Dropping message...");
					return null;
				}
			}

		return msg;
	}

	private void broadcast(WorkMessage msg) {
		System.out.println("Forwarding message...");
		WorkMessage.Builder wb = WorkMessage.newBuilder(msg);
		Header.Builder hb = Header.newBuilder(msg.getHeader());
		hb.setMaxHops(hb.getMaxHops() - 1);
		wb.setHeader(hb);
		state.getEmon().broadcastMessage(wb.build());
	}

}