package client.globalKeyListener;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import client.view.StopWatchView;

public class MyGlobalKeyListener implements NativeKeyListener {
	private StopWatchView swv;
	private boolean saveTheNextKeystroke = true;
	private int key;

	public MyGlobalKeyListener(StopWatchView swv) {
		this.swv = swv;
	}

	public void nativeKeyPressed(NativeKeyEvent e) {
		System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
		if (saveTheNextKeystroke) {
			saveTheNextKeystroke = false;
			key = e.getKeyCode();
			swv.btnKey.setText(NativeKeyEvent.getKeyText(e.getKeyCode())+"");
			return;
		}
		if (e.getKeyCode() == key) {
			swv.btnStopStartCount.doClick();
		}
	}

	public void nativeKeyReleased(NativeKeyEvent e) {
		System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
	}

	public void nativeKeyTyped(NativeKeyEvent e) {
		System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
	}
}
