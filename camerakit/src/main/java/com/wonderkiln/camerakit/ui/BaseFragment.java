package com.wonderkiln.camerakit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wonderkiln.camerakit.core.R;

public class BaseFragment extends Fragment {

    protected FragmentActivity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    /**
     *适合有确定按钮的提示对话框
     * @param msgTitle
     * @param msg
     * @param
     */
    public void showHintDialog(final DialogClickLinear linear, String msgTitle, String msg, String cancelText, String defineText, boolean isCancellab) {
        try{
            AlertDialog.Builder builer = new  AlertDialog.Builder(mActivity,R.style.Dialog_Fullscreen);
            LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            View view = layoutInflater.inflate(R.layout.dialog_base_hint, null);
            TextView tvTitle = (TextView)view.findViewById(R.id.dialog_title);
            tvTitle.setText(msgTitle);
            TextView tvMsg = (TextView)view.findViewById(R.id.dialog_msg);
            tvMsg.setText(msg);
            TextView tvBt = (TextView)view.findViewById(R.id.dialog_bt_hint);
            tvBt.setVisibility(View.GONE);
            LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.dialog_bt_layout);
            linearLayout.setVisibility(View.VISIBLE);
            Button btCancel = (Button) view.findViewById(R.id.dialog_cancel);
            btCancel.setText(cancelText);
            Button btDefine = (Button) view.findViewById(R.id.dialog_define);
            btDefine.setText(defineText);
            builer.setCancelable(isCancellab);
            //builer.setView(view);
            final AlertDialog dialog = builer.create();
            dialog.show();
            btCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (linear != null) {
                        linear.cancelClick();
                    }
                }
            });
            btDefine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (linear != null) {
                        linear.defineClick();
                    }
                }
            });
            Window dialogWindow = dialog.getWindow();
            dialogWindow.setContentView(view);
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager m = getActivity().getWindowManager();
            Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            //p.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.3，根据实际情况调整
            p.width = (int) (d.getWidth() * 0.73); // 宽度设置为屏幕的0.7，根据实际情况调整
            dialogWindow.setAttributes(p);
        }catch (Exception e){
            e.getStackTrace();
        }
    }

    public void showHintDialog(final DialogDefineClick dialogDefineClick, String msgTitle, String msg, String posiText, boolean isCancellab) {
        try {
            AlertDialog.Builder builer = new AlertDialog.Builder(mActivity,R.style.Dialog_Fullscreen);
            LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            View view = layoutInflater.inflate(R.layout.dialog_base_hint, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.dialog_title);
            tvTitle.setText(msgTitle);
            TextView tvMsg = (TextView) view.findViewById(R.id.dialog_msg);
            tvMsg.setText(msg);
            TextView tvBt = (TextView) view.findViewById(R.id.dialog_bt_hint);
            tvBt.setText(posiText);
            //builer.setView(view);
            builer.setCancelable(isCancellab);
            final AlertDialog dialog = builer.create();
            dialog.show();
            tvBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (dialogDefineClick != null) {
                        dialogDefineClick.defineClick();
                    }
                }
            });
            Window dialogWindow = dialog.getWindow();
            dialogWindow.setContentView(view);
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager m = getActivity().getWindowManager();
            Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            //p.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.3，根据实际情况调整
            p.width = (int) (d.getWidth() * 0.73); // 宽度设置为屏幕的0.7，根据实际情况调整
            dialogWindow.setAttributes(p);
        }catch (Exception e){
            e.getStackTrace();
        }
    }

    public interface  DialogClickLinear {

        public void cancelClick();

        public void defineClick();

    }

    public interface  DialogDefineClick {
        public void defineClick();
    }
}
