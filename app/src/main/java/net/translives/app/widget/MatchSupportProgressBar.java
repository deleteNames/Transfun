package net.translives.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import net.translives.app.R;

import java.util.ArrayList;
import java.util.List;


public class MatchSupportProgressBar extends View {
	/**
	 * 朿新更新优匿
	 * 1 增加调节圆角矩形弧度变量
	 * 2 当大亿95%不绘制caver矩形
	 * 3 当大大于50时，mpercentcolor变色
	 * 4 新增动画效果
	 * 5 优化动画效果
	 * 6 可在XML中设置tagText
	 * 待优匿
	 * 垂直的padding问题
	 */
	private static final float SCALSE = 3*1.0f/4 ;
	private static final float SCALSE4_5 = 4*1.0f/5 ;

	private static int DEFAULT_LINE_NUMBER;

	private static final int DEFAULT_PROGRESS_RADIO=5;// 进度条圆角弧庿

	private static final int DEFAULT_OFFSETX=5;// 标签要显示的内容
	private static final int DEFAULT_OFFSETR=5;// 标签要显示的内容
	private static final int DEFAULT_LINE_SPACING=5;// 标签要显示的内容

	private static final String DEFAULT_TAG_TEXT="主胜;广;主负";// 标签要显示的内容


	private static final String DEFAULT_PERCENT_TEXT="0";// 标签要显示的内容

	private static final int DEFAULT_SIZE_TAG_TEXT=15;// 标签文字默认尺寸
	private static final int DEFAULT_COLOR_TAG_TEXT=0XFFFC00D1;//标签文字默认颜色

	private static final int DEFAULT_COLOR_REACH=0XFFD3D6DA;//已填充部分的颜色
	private static final int DEFAULT_HEIGHT_REACH=10;//已填充部分的高度

	private static final int DEFAULT_HEIGHT_BACKGROUND=10;//已填充部分的高度
	private static final int DEFAULT_COLOR_BACKGROUND=0XFFD3D6DA;//未填充部分的颜色

	private static final int DEFAULT_COLOR_PERCENT=0XFFFC00D1;//百分比字体的颜色
	private static final int DEFAULT_SIZE_PERCENT=10;//百分比字体的颜色

	private int offsetX = dp2px(DEFAULT_OFFSETX);
	private int offsetR = dp2px(DEFAULT_OFFSETR);
	private int offsetRigh;
	private int offsetForBitmap;

	private OnRightTextClickListener mOnRightTextClickListener;

	/**
	 * 是否是投过票状濿
	 * true 是投迿
	 * false 是未投过
	 */
	private boolean stateBeSupported =false;

	private int mLineSpacing=dp2px(DEFAULT_LINE_SPACING);

	private List<Line> mLineList = new ArrayList<Line>();

	private int mTagTextSize = sp2px(DEFAULT_SIZE_TAG_TEXT);
	private int mTagTextColor = DEFAULT_COLOR_TAG_TEXT;

	private int mProgressRadio = DEFAULT_PROGRESS_RADIO;

	private int mReachHeight = dp2px(DEFAULT_HEIGHT_REACH);
	private int mReachColor = DEFAULT_COLOR_REACH;

	private int mBackGroundHeight = dp2px(DEFAULT_HEIGHT_BACKGROUND);
	private int mBackGroundColor = DEFAULT_COLOR_BACKGROUND;

	private int mPercentColor = DEFAULT_COLOR_PERCENT;
	private int mPercentSize = sp2px(DEFAULT_SIZE_PERCENT);

	private String mTagTextString = DEFAULT_TAG_TEXT;

	private ArrayList<String> mTagTextList = new ArrayList<String>();

	private Paint mPaint = new Paint();

	private Bitmap mBitmap;
	private int mRealWidth;

	private int bitmapWidth;
	private int bitmapHeight;
	/**
	 * 判断右侧百分比文字是否变艿
	 */
	private boolean mPercentTextChangeColor;

	public MatchSupportProgressBar(Context context) {
		this(context, null);
	}

	public MatchSupportProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MatchSupportProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		obtainStyledAttrs(attrs);
	}

	/**
	 * 获取自定义属怿
	 * @param attrs
	 */
	private void obtainStyledAttrs(AttributeSet attrs) {
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.MatchSupportProgressBar);
		if(typedArray.getString(R.styleable.MatchSupportProgressBar_progress_radio)!=null){
			mProgressRadio = Integer.parseInt(typedArray.getString(R.styleable.MatchSupportProgressBar_progress_radio));
		}
		if(typedArray.getString(R.styleable.MatchSupportProgressBar_progress_tag_text)!=null){
			mTagTextString = typedArray.getString(R.styleable.MatchSupportProgressBar_progress_tag_text);
		}
		mTagTextSize = (int) typedArray.getDimension(R.styleable.MatchSupportProgressBar_progress_tag_text_size,mTagTextSize);
		mTagTextColor =  typedArray.getColor(R.styleable.MatchSupportProgressBar_progress_tag_text_color,mTagTextColor);

		mReachColor = typedArray.getColor(R.styleable.MatchSupportProgressBar_progress_reach_color,mReachColor);
		mReachHeight = (int) typedArray.getDimension(R.styleable.MatchSupportProgressBar_progress_reach_height,mReachHeight);

		mBackGroundColor = typedArray.getColor(R.styleable.MatchSupportProgressBar_progress_background_color,mBackGroundColor);
		mBackGroundHeight = (int) typedArray.getDimension(R.styleable.MatchSupportProgressBar_progress_background_height,mBackGroundHeight);

		mPercentColor =  typedArray.getColor(R.styleable.MatchSupportProgressBar_progress_percent_text_color,mPercentColor);
		mPercentSize = (int) typedArray.getDimension(R.styleable.MatchSupportProgressBar_progress_percent_text_size,mPercentSize);

		offsetX = (int) typedArray.getDimension(R.styleable.MatchSupportProgressBar_progress_offsetX,offsetX);
		offsetR = (int) typedArray.getDimension(R.styleable.MatchSupportProgressBar_progress_offsetR,offsetR);
		mLineSpacing = (int) typedArray.getDimension(R.styleable.MatchSupportProgressBar_progress_LineSpacing,mLineSpacing);

		typedArray.recycle();

		initView();
	}

	/*
	 * 默认状濁下都是 未投票状态?
	 * 接口访问成功 必须要调用setPercentState
	 */
	private void initView() {
		stateBeSupported=false;//初始化时首先将其设置丿0

		bitmapWidth = (int) (mBackGroundHeight*SCALSE*SCALSE);
		bitmapHeight = (int )(mBackGroundHeight*SCALSE*1.0f/2);
		mPaint.setTextSize(Math.max(mTagTextSize,mPercentSize));

		mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.froum_match_select_);
		mBitmap=Bitmap.createScaledBitmap(mBitmap,bitmapWidth,bitmapHeight,false);

		//		Log.d("TTT","mTagTextString"+mTagTextString);
		String[] mTagStrArray = mTagTextString.split(";");
		int length = mTagStrArray.length;
		for (int i = 0; i < length; i++) {
			mTagTextList.add(mTagStrArray[i]);
		}
		DEFAULT_LINE_NUMBER=mTagTextList.size();
		mPercentTextChangeColor=false;
		for(int i =0;i<DEFAULT_LINE_NUMBER;i++){
			Line line = new Line();
			line.mTagText = mTagTextList.get(i);
			line.mPercentText=DEFAULT_PERCENT_TEXT;
			line.rectTop = i*mLineSpacing+i*mBackGroundHeight;
			line.rectBottom = mBackGroundHeight+i*(mBackGroundHeight+mLineSpacing);
			line.isChecked = false;
			line.bitmapY = ((mBackGroundHeight-bitmapHeight)/2)+i*(mBackGroundHeight+mLineSpacing);
			mLineList.add(line);

		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);

		setMeasuredDimension(width,height);
		mRealWidth = getMeasuredWidth()-getPaddingLeft()-getPaddingRight();
		saveSupportBtn(width);
	}

	/**
	 * 当控件测量完成后，保存一下右侧文字区域范囿
	 * @param width
	 */
	private void saveSupportBtn(int width) {
		offsetRigh=(int) (SCALSE4_5*mRealWidth)+offsetR;
		for(int i=0,length=mLineList.size();i<length;i++){
			mLineList.get(i).supportBtnL=getPaddingLeft()+offsetRigh;
			mLineList.get(i).supportBtnT=(int) (mBackGroundHeight*(1-SCALSE)+i*(mLineSpacing+mBackGroundHeight));
			mLineList.get(i).supportBtnB=(int) (mBackGroundHeight*SCALSE+i*(mLineSpacing+mBackGroundHeight));
			mLineList.get(i).supportBtnR=width-getPaddingRight();
		}
	}

	/**
	 * 测量自定义view实际的高庿
	 * @return
	 * @param heightMeasureSpec
	 */
	private int measureHeight(int heightMeasureSpec) {
		int result = 0;
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		if(heightMode==MeasureSpec.EXACTLY){
			result = heightSize;
		}
		else{
			int textHeight = (int) (mPaint.descent()-mPaint.ascent());
			result = getPaddingBottom()+getPaddingTop()+DEFAULT_LINE_NUMBER*(Math.max(Math.max(mReachHeight,mBackGroundHeight),Math.abs(textHeight)))+(DEFAULT_LINE_NUMBER-1)*mLineSpacing;	

			if(heightMode==MeasureSpec.AT_MOST){
				return Math.min(result,heightSize);
			}
		}
		return result;
	}

	@SuppressLint("NewApi") @Override
	protected void onDraw(Canvas canvas) {

		/*
		 * 1 画一丿 圆角矩形 这是整个progressbar的长庿 BackGround 
		 * 2 再画丿个圆角矩彿 这是已填充进度的长度 ReachBar
		 * 3 再画丿个矩彿 覆盖 ReachBar的右边界
		 * 	3.5 绘制对号图片
		 * 		如果是被选择的item 那么绘制图片
		 * 			偏移offsetX的距禿
		 * 			绘制bitmap
		 * 			重新计算offsetX 用于绘制接下来的tagText
		 * 			
		 * 4 绘制左侧tagText
		 * 5 绘制右侧percentText
		 * 
		 * 
		 */
		canvas.save();

		canvas.translate(getPaddingLeft(),0);//把画布移动到CenterVertical

		//draw BackGround bar
		mPaint.setAntiAlias(true);                       //设置画笔为无锯齿
		mPaint.setStyle(Paint.Style.FILL);              //充满
		//设置画笔颜色
		mPaint.setColor(mBackGroundColor);   

		for(int i=0,length=mLineList.size();i<length;i++){
			RectF rb=new RectF(0,mLineList.get(i).rectTop,mRealWidth,mLineList.get(i).rectBottom);   //RectF对象
			canvas.drawRoundRect(rb, mProgressRadio, mProgressRadio, mPaint);        //绘制圆角矩形
		}
		float progressX = 0;
		//如果为?中状濁，才可以绘刿 reachbar and bitmap
		if(stateBeSupported){
			//draw ReachBar
			mPaint.setColor(mReachColor);
			for(int i=0,length=mLineList.size();i<length;i++){
				progressX =Integer.parseInt(mLineList.get(i).mPercentText)*1.0f/100*mRealWidth; //已填充部分的x坐标
				RectF rr=new RectF(0,mLineList.get(i).rectTop,progressX,mLineList.get(i).rectBottom);
				canvas.drawRoundRect(rr, mProgressRadio, mProgressRadio, mPaint);        //绘制圆角矩形
				if(progressX>offsetRigh){
					mPercentTextChangeColor=true;
				}
				if(Integer.parseInt(mLineList.get(i).mPercentText)<=95){
					//draw react to cover RoundRect rightpart
					RectF rectF=new RectF(progressX/2,mLineList.get(i).rectTop,progressX,mLineList.get(i).rectBottom);   
					canvas.drawRect(rectF, mPaint);
				}
			}

			//draw check bitmap
			for(int i=0,length=mLineList.size();i<length;i++){
				if(mLineList.get(i).isChecked==true){
					canvas.drawBitmap(mBitmap,offsetX,mLineList.get(i).bitmapY,null);
					offsetForBitmap = offsetX+mBitmap.getWidth()+offsetX;
				}
			}
		}


		//draw TagText
		mPaint.setColor(mTagTextColor);
		mPaint.setTextSize(mTagTextSize);
		FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();  

		for(int i=0,length=mLineList.size();i<length;i++){
			RectF rr=new RectF(0,mLineList.get(i).rectTop,mRealWidth,mLineList.get(i).rectBottom);
			int baseline = (int) ((rr.bottom + rr.top - fontMetrics.bottom - fontMetrics.top) / 2);  
			if(mLineList.get(i).isChecked==true){
				canvas.drawText(mLineList.get(i).mTagText,offsetForBitmap, baseline, mPaint);  
			}else{
				canvas.drawText(mLineList.get(i).mTagText,offsetX, baseline, mPaint);  
			}
		}

		//draw percentText

		for(int i=0,length=mLineList.size();i<length;i++){
			mPaint.setColor(mPercentColor);
			mPaint.setTextSize(mTagTextSize);
			RectF rr=new RectF(0,mLineList.get(i).rectTop,mRealWidth,mLineList.get(i).rectBottom);
			int baseline = (int) ((rr.bottom + rr.top - fontMetrics.bottom - fontMetrics.top) / 2);  
			//如果为?中状濿 显示 百分毿 ，如果是未被选中状濿 则显礿 “支持?
			if(stateBeSupported){
				if(mPercentTextChangeColor){
					mPaint.setColor(mTagTextColor);
				}
				canvas.drawText(mLineList.get(i).mPercentText+"%",offsetRigh , baseline, mPaint);
			}else{
				canvas.drawText("支持",offsetRigh , baseline, mPaint);
			}
		}

		canvas.restore();
	}

	boolean isCover=false;
	int result;
	int x ;
	int	y ;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(stateBeSupported==true){
			return false;
		}
		int action = event.getAction();
		if(action == MotionEvent.ACTION_DOWN){
			Log.d("TAG","ACTION_DOWN");
			x = (int) event.getX();
			y = (int) event.getY();
			result = checkBtnRange(x, y);

			if(result<mLineList.size()){
				isCover=true;
			}else{
				return false;
			}
		}

		if(action == MotionEvent.ACTION_MOVE){
			int moveX = (int) event.getX();
			int moveY = (int) event.getY();
			if(Math.abs(moveY-y)>50||Math.abs(moveX-x)>50){
				Log.d("TAG","Math.abs(moveY-y)"+Math.abs(moveY-y)+"Math.abs(moveX-x)"+Math.abs(moveX-x));
				isCover=false;
			}
		}

		if(action == MotionEvent.ACTION_UP){
			Log.d("TAG","isCover"+isCover);
			if(isCover){
				Log.d("TAG","ACTION_UP");
				if(mOnRightTextClickListener!=null){
					mOnRightTextClickListener.onClick(result);
				}
			}
		}
		return true;
	}

	/**
	 * 判断手的触点是否在文字范围内
	 * @param x
	 * @param y
	 * @return 手指点击的位置在第几衿
	 */
	private int checkBtnRange(int x, int y) {
		for(int i=0,length=mLineList.size();i<length;i++){
			int l = mLineList.get(i).supportBtnL;
			int t = mLineList.get(i).supportBtnT;
			int r = mLineList.get(i).supportBtnR;
			int b = mLineList.get(i).supportBtnB;
			Log.i("TAG","l="+l+"t="+t+"r="+r+"b="+b);
			if(x> l && x<r && y<b&& y>t){
				Log.i("TAG","理論上該响应 i="+i);
				return i;
			}
		}
		return 100;
	}

	/**
	 * 初始化三个进度条的进庿
	 * @param percentList
	 * @param state true 表示投票过，false表示未投迿
	 * @param flag true 表示显示动画效果，false不显示动画效枿
	 */
	public void setPercentState(ArrayList<String> percentList,ArrayList<Boolean> isCheckedList,boolean state,boolean flag){
		mPercentTextChangeColor=false;
		if(state){
			stateBeSupported=state;
			for(int i=0,length=mLineList.size();i<length;i++){
				mLineList.get(i).mPercentText = percentList.get(i);
				mLineList.get(i).isChecked = isCheckedList.get(i);
			}
			if(flag){
				animotionProgress(percentList);
			}else{
				invalidate();
			}
		}
	}

	/**
	 * 从新初始化进度条状濁，以防出现listView复用等问颿
	 * @param state
	 */
	public void setState(boolean state){
		mPercentTextChangeColor=false;
		stateBeSupported=state;
		for(int i=0,length=mLineList.size();i<length;i++){
			mLineList.get(i).isChecked = false;
		}
		invalidate();
	}

	private Handler handler=new Handler();

	int time =0;
	int allTiem=15;
	ArrayList<Integer> mPercentList = new ArrayList<Integer>();
	/**
	 * 进度条动画效枿
	 * @param percentList
	 */
	private void animotionProgress(ArrayList<String> percentList){
		int length =percentList.size();

		final int[] aArray = new int[length];  
		//每一条progress每一帧应该增加的距离 卿 速度
		final float[] tempArr = new float[length];  


		//将percent转为int数组
		for(int i=0;i<length;i++){
			aArray[i] =Integer.parseInt(percentList.get(i));
			Integer integer  = Integer.valueOf(0);
			mPercentList.add(integer);
		}


		//初始匿 tempArr
		for(int k =0;k<length;k++){
			tempArr[k]=aArray[k]*1.0f/allTiem;
		}
		
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {


				if(time==allTiem){
					for(int i=0;i<DEFAULT_LINE_NUMBER;i++){
						mLineList.get(i).mPercentText=aArray[i]+"";
					}
					time=0;
					invalidate();
				}else{
					for(int i=0;i<DEFAULT_LINE_NUMBER;i++){
						mLineList.get(i).mPercentText=mPercentList.get(i)+"";
					}
					for(int j =0;j<DEFAULT_LINE_NUMBER;j++){
						if(tempArr[j]*time<=aArray[j]){
							mPercentList.set(j, (int)(tempArr[j]*time));
						}
					}
					time++;
					invalidate();
					handler.postDelayed(this, 20);
				}

			}
		}, 20);
	}



	private int dp2px(int dpVal){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpVal,getResources().getDisplayMetrics());
	}

	private int sp2px(int spVal){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spVal,getResources().getDisplayMetrics());
	}

	public interface OnRightTextClickListener{
		void onClick(int position);
	}

	public void setOnRightTextClickListener(OnRightTextClickListener onRightTextClickListener){
		mOnRightTextClickListener=onRightTextClickListener;
	}

	public static class Line{

		/**
		 * 是否绘制选中图标
		 */
		private boolean isChecked = false;

		private int rectTop;

		private int rectBottom;

		private String mPercentText = "";

		private String mTagText = "";

		private int bitmapY;

		private int supportBtnL;
		private int supportBtnT;
		private int supportBtnR;
		private int supportBtnB;

	}
}
