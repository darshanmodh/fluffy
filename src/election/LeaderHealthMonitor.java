package election;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: codepenman.
 * @date: 4/1/16
 */
public class LeaderHealthMonitor {

	private final LeaderHealthListener healthListener;
	private boolean debug = false;
	private final Logger logger = LoggerFactory.getLogger("Leader Health Monitor");
	private AtomicBoolean stop;
	private HealthMonitorTask task;
	private AtomicLong beatTime;
	private AtomicBoolean isRunning;
	private final long timeout;

	public LeaderHealthMonitor(LeaderHealthListener healthListener, long timeout) {
		this.healthListener = healthListener;
		this.timeout = timeout;
		task = new HealthMonitorTask();
		stop = new AtomicBoolean(false);
		isRunning = new AtomicBoolean (false);
		beatTime = new AtomicLong(Long.MAX_VALUE);
	}

	public void start() {
		/* Start the task, only if it is not started */
		//if(!isRunning.get ())   {
		logger.info("~~~~~~~~Follower - Started Leader Monitor");
		stop.getAndSet (false);
		task.start();
		isRunning.getAndSet (true);
		//}
	}
/*

	public boolean isRunning() {
		return isRunning.get ();
	}

	public void cancel() {
		*/
/* Cancel the task, only if it is not stopped before *//*

		//if(stop.get ()) {
		task.interrupt ();
		stop.getAndSet(true);
		isRunning.getAndSet (false);
		task = new HealthMonitorTask ();
		beatTime.getAndSet (Long.MAX_VALUE);
		logger.info("~~~~~~~~Follower - Cancelled Leader Monitor");
		//}
	}
*/

	public void onBeat(long beatTime) {
		this.beatTime.getAndSet(beatTime);
	}

	private class HealthMonitorTask extends Thread {

		@Override
		public void run() {
			try {
				while (!stop.get()) {
					if (debug)
						logger.info("********Started: " + new Date(System.currentTimeMillis()));

					long currentTime = System.currentTimeMillis();

					//logger.info("*****Last heart beat received from Leader: " + (currentTime - beatTime.get()));
					if ((currentTime - beatTime.get()) > timeout) {
						healthListener.onLeaderBadHealth();
						//break;
					}
					synchronized (this) {
						wait((long) (timeout * 0.9));
					}
				}
			} catch (InterruptedException e) {
				logger.info("********Timer was interrupted: " + new Date(System.currentTimeMillis()));
			}
		}
	}
}
