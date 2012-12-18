package com.ssr.rtc;

import java.io.IOException;
import java.util.List;

import com.ssr.experimental.*;

import jp.co.sec.rtm.*;

/**
 *
 */
public class RoombaTesterImpl extends RTCBase {
	private static final String TAG = "MyRTC";

	private static RoombaTester	myRTC = null;
	private RTCService			rtcService;

	// InPort
	private InPort<TimedOctetSeq>		inPortOctetSeq;

	// OutPort
	private OutPort<TimedString>		outPortString;
	private OutPort<TimedDoubleSeq>     outPortDoubleSeq;

	/**
	 * コンストラクタ
	 *
	 * @param myRTC
	 * @param rtcService
	 * @param mode データ型
	 */
	public RoombaTesterImpl(RoombaTester myRTC, RTCService rtcService) {
		Logger4RTC.debug(TAG, "RTMonAndroidImpl");
		RoombaTesterImpl.myRTC = myRTC;
		this.rtcService = rtcService;
		rtcService.setRTC(this);

		initInPort();
		initOutPort();
	}

	/**
	 * 初期化処理.コンポーネントライフサイクルの開始時に一度だけ呼ばれる.
	 */
	@Override
	public int onInitialize() {
		Logger4RTC.debug(TAG, "onInitialize");
		myRTC.showToast("onInitialize");
		return ReturnCode.RTC_RTC_OK;
	}

	/**
	 * 非アクティブ状態からアクティブ化されるとき1度だけ呼ばれる.
	 */
	@Override
	public int onActivated() {
		Logger4RTC.debug(TAG, "onActivated");
		myRTC.showToast("onActivate");
		return ReturnCode.RTC_RTC_OK;
	}

	/**
	 * アクティブ状態時に周期的に呼ばれる.
	 */
	@Override
	public int onExecute() {
		Logger4RTC.debug(TAG, "onExecute");
		ioControl();
		return ReturnCode.RTC_RTC_OK;
	}

	/**
	 * アクティブ状態から非アクティブ化されるとき1度だけ呼ばれる.
	 */
	@Override
	public int onDeactivated() {
		Logger4RTC.debug(TAG, "onDeactivated");
		myRTC.showToast("onDectivate");
		return ReturnCode.RTC_RTC_OK;
	}

	/**
	 * エラー状態に入る前に1度だけ呼ばれる.
	 */
	@Override
	public int onAborting() {
		Logger4RTC.debug(TAG, "onAborting");
		myRTC.showToast("onAborting");
		return ReturnCode.RTC_RTC_OK;
	}

	/**
	 * エラー状態からリセットされ非アクティブ状態に移行するときに1度だけ呼ばれる.
	 */
	@Override
	public int onReset() {
		Logger4RTC.debug(TAG, "onReset");
		myRTC.showToast("onReset");
		return ReturnCode.RTC_RTC_OK;
	}

	/**
	 * エラー状態にいる間周期的に呼ばれる.
	 */
	@Override
	public int onError() {
		Logger4RTC.debug(TAG, "onError");
		myRTC.showToast("onError");
		return ReturnCode.RTC_RTC_OK;
	}

	/**
	 * コンポーネントライフサイクルの終了時に1度だけ呼ばれる.
	 */
	@Override
	public int onFinalize() {
		Logger4RTC.debug(TAG, "onFinalize");
		myRTC.showToast("onFinalize");
		return ReturnCode.RTC_RTC_OK;
	}

	/**
	 * InPort初期化
	 */
	private void initInPort(){
		String pName = RoombaTesterProfile.InPort1;
		TimedOctetSeq tm = new TimedOctetSeq();
		inPortOctetSeq = new InPort<TimedOctetSeq>(pName, tm);
		rtcService.addInPort(inPortOctetSeq);
	}

	/**
	 * OutPort初期化
	 */
	private void initOutPort(){
		String pName = RoombaTesterProfile.OutPort1;
		TimedDoubleSeq tm = new TimedDoubleSeq();
		outPortDoubleSeq = new OutPort<TimedDoubleSeq>(pName, tm);
		rtcService.addOutPort(outPortDoubleSeq);
		TimedString tm2 = new TimedString();
		outPortString = new OutPort<TimedString>(pName, tm2);
		rtcService.addOutPort(outPortString);
	}

	/**
	 * 入出力コントロール
	 */
	private void ioControl(){
		Logger4RTC.spec("SPEC_LOG", "RTM_11j: onExecute in Application Started.");
		String str = "";
		try{
			if (inPortOctetSeq.isNew()){						// 入力ポートに新しいデータが入ったか確認する。
				TimedOctetSeq to = inPortOctetSeq.read();         // データをinPortから取り出す。
				List<Byte> n = to.getData();
//				tl.setData(n*5);
//				outPortLong.write(tl);						// データをoutPortに書き出す。
//				str = tl.getTm().toString() +" | "+n;
//				myRTC.textDraw(str);		// 画面表示			
			}
			
			
		}
		catch (IOException e){					// InPortの読み出しでExceptionが発生する可能性があるので、ここでキャッチ
		}
		Logger4RTC.debug(TAG, "onExecute value="+str);
		Logger4RTC.spec("SPEC_LOG", "RTM_18j: onExecute in Application Ended.");
	}
}
