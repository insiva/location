package com.matteo.matteolocationhistory;

class LockScreanThread extends Thread{
	LockActivity la;LockScreen ls;
	public LockScreanThread(LockActivity _la,LockScreen _ls){
		la=_la;
		ls=_ls;
	}
	@Override
	public void run()
	{
		la.Do();
		HistoryActivity ha=(HistoryActivity)la;
		ha.hdlHistory.sendEmptyMessage(LockScreen.FlagLockEnd);
	}
}
