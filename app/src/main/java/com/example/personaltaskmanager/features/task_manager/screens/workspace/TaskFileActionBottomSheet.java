package com.example.personaltaskmanager.features.task_manager.screens.workspace;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks.NotionBlock;

public class TaskFileActionBottomSheet extends BottomSheetDialogFragment {

    public interface Listener {
        void onDelete(NotionBlock block);
        void onDuplicate(NotionBlock block);
        void onMove(NotionBlock block);
    }

    private NotionBlock block;
    private Listener listener;

    public TaskFileActionBottomSheet(NotionBlock block, Listener listener) {
        this.block = block;
        this.listener = listener;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.feature_task_manager_file_actions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {

        // Cập nhật title theo loại block
        TextView tvTitle = v.findViewById(R.id.tv_action_title);
        String title = "Block actions";
        switch (block.type) {
            case FILE:
                title = "File actions";
                break;
            case PARAGRAPH:
                title = "Paragraph actions";
                break;
            case TODO:
                title = "Todo actions";
                break;
            case BULLET:
                title = "Bullet actions";
                break;
            case DIVIDER:
                title = "Divider actions";
                break;
        }
        tvTitle.setText(title);

        // Chỉ hiện View và Copy link cho FILE block
        View btnView = v.findViewById(R.id.btn_action_view);
        View btnCopy = v.findViewById(R.id.btn_action_copy);
        
        if (block.type == NotionBlock.Type.FILE) {
            btnView.setVisibility(View.VISIBLE);
            btnCopy.setVisibility(View.VISIBLE);
            
            btnView.setOnClickListener(view -> {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.parse(block.fileUri), "*/*");
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Cannot open file", Toast.LENGTH_SHORT).show();
                }
                dismiss();
            });

            btnCopy.setOnClickListener(view -> {
                ClipboardManager cm =
                        (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("file", block.fileUri));
                Toast.makeText(getContext(), "Copied", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        } else {
            btnView.setVisibility(View.GONE);
            btnCopy.setVisibility(View.GONE);
        }

        v.<TextView>findViewById(R.id.btn_action_duplicate).setOnClickListener(view -> {
            if (listener != null) listener.onDuplicate(block);
            dismiss();
        });

        v.<TextView>findViewById(R.id.btn_action_move).setOnClickListener(view -> {
            if (listener != null) listener.onMove(block);
            dismiss();
        });

        v.<TextView>findViewById(R.id.btn_action_delete).setOnClickListener(view -> {
            if (listener != null) listener.onDelete(block);
            dismiss();
        });
    }
}
