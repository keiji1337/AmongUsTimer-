package model;

import client.view.StopWatchView;

public class Timer implements Runnable {
	private int min = 10, sec = 10;
	public boolean countOrStop = false;
	private StopWatchView swv;

	public Timer(StopWatchView swv) {
		this.swv = swv;
	}

	public void run() {
		while (true) {
			if (this.countOrStop) {
				if (this.min != 0 || this.sec != 0) {
					if (this.sec <= 0) {
						this.sec = 60;
						if (this.min > 0)
							this.min--;
					}
					this.sec--;
					if (swv != null && (this.min >= 0 || this.sec >= 0)) {
						swv.SetTime(this.min + "", this.sec + "");
					}
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public int GetMin() {
		return this.min;
	}

	public int GetSec() {
		return this.sec;
	}

	public void Change(boolean value) {
		System.out.println("change value time " + value);
		this.countOrStop = value;
	}

	public void setTime(int min, int sec) {
		System.out.println("setTime newTime " + min + " - " + sec);
		this.min = min;
		this.sec = sec;
		if (swv != null) {
			swv.SetTime(this.min + "", this.sec + "");
		}
	}
}
