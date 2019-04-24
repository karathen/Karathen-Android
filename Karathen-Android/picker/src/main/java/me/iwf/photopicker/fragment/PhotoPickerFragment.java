package me.iwf.photopicker.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.hss01248.image.ImageLoader;
import com.hss01248.image.config.GlobalConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.PhotoPreview;
import me.iwf.photopicker.R;
import me.iwf.photopicker.adapter.PhotoGridAdapter;
import me.iwf.photopicker.adapter.PopupDirectoryListAdapter;
import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.entity.PhotoDirectory;
import me.iwf.photopicker.event.OnPhotoClickListener;
import me.iwf.photopicker.utils.ImageCaptureManager;
import me.iwf.photopicker.utils.MediaStoreHelper;
import me.iwf.photopicker.widget.Titlebar;

import static android.app.Activity.RESULT_OK;
import static me.iwf.photopicker.PhotoPicker.DEFAULT_COLUMN_NUMBER;
import static me.iwf.photopicker.PhotoPicker.EXTRA_PREVIEW_ENABLED;
import static me.iwf.photopicker.PhotoPicker.EXTRA_SHOW_GIF;
import static me.iwf.photopicker.utils.MediaStoreHelper.INDEX_ALL_PHOTOS;

/**
 * Created by donglua on 15/5/31.
 */
public class PhotoPickerFragment extends Fragment {

  private ImageCaptureManager captureManager;
  private PhotoGridAdapter photoGridAdapter;

  private PopupDirectoryListAdapter listAdapter;
  //所有photos的路径
  private List<PhotoDirectory> directories;
  //传入的已选照片
  private ArrayList<String> originalPhotos;

  private int SCROLL_THRESHOLD = 30;
  int column;
  //目录弹出框的一次最多显示的目录数目
  public static int COUNT_MAX = 4;
  private final static String EXTRA_CAMERA = "camera";
  private final static String EXTRA_COLUMN = "column";
  private final static String EXTRA_COUNT = "count";
  private final static String EXTRA_GIF = "gif";
  private final static String EXTRA_ORIGIN = "origin";
  private ListPopupWindow listPopupWindow;
  //private RequestManager mGlideRequestManager;
  private Context mContext;

  private Titlebar titlebar;

  public static PhotoPickerFragment newInstance(boolean showCamera, boolean showGif,
      boolean previewEnable, int column, int maxCount, ArrayList<String> originalPhotos) {
    Bundle args = new Bundle();
    args.putBoolean(EXTRA_CAMERA, showCamera);
    args.putBoolean(EXTRA_GIF, showGif);
    args.putBoolean(EXTRA_PREVIEW_ENABLED, previewEnable);
    args.putInt(EXTRA_COLUMN, column);
    args.putInt(EXTRA_COUNT, maxCount);
    args.putStringArrayList(EXTRA_ORIGIN, originalPhotos);
    PhotoPickerFragment fragment = new PhotoPickerFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    mContext = context;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);

    //mGlideRequestManager = Glide.with(this);

    directories = new ArrayList<>();
    originalPhotos = getArguments().getStringArrayList(EXTRA_ORIGIN);

    column = getArguments().getInt(EXTRA_COLUMN, DEFAULT_COLUMN_NUMBER);
    boolean showCamera = getArguments().getBoolean(EXTRA_CAMERA, true);
    boolean previewEnable = getArguments().getBoolean(EXTRA_PREVIEW_ENABLED, true);

    photoGridAdapter = new PhotoGridAdapter(mContext,  directories, originalPhotos, column);
    photoGridAdapter.setShowCamera(showCamera);
    photoGridAdapter.setPreviewEnable(previewEnable);

    Bundle mediaStoreArgs = new Bundle();

    boolean showGif = getArguments().getBoolean(EXTRA_GIF);
    mediaStoreArgs.putBoolean(EXTRA_SHOW_GIF, showGif);
    MediaStoreHelper.getPhotoDirs(getActivity(), mediaStoreArgs,
        new MediaStoreHelper.PhotosResultCallback() {
          @Override public void onResultCallback(List<PhotoDirectory> dirs) {
            directories.clear();
            directories.addAll(dirs);
            photoGridAdapter.notifyDataSetChanged();
            listAdapter.notifyDataSetChanged();
            adjustHeight();
          }
        });

    captureManager = new ImageCaptureManager(getActivity());
  }


  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    Log.e("fragment","onCreateView----picker");
    final View rootView = inflater.inflate(R.layout.__picker_fragment_photo_picker, container, false);
    titlebar = (Titlebar) rootView.findViewById(R.id.titlebar);

    listAdapter  = new PopupDirectoryListAdapter( directories);

    RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_photos);
    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(column, OrientationHelper.VERTICAL);
    layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(photoGridAdapter);

    recyclerView.setItemAnimator(new DefaultItemAnimator());

    final Button btSwitchDirectory = (Button) rootView.findViewById(R.id.button);

    Button btnPreview = (Button) rootView.findViewById(R.id.btn_preview);

    listPopupWindow = new ListPopupWindow(getActivity());

    listPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//替换背景
    WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    int widths = wm.getDefaultDisplay().getWidth();
    listPopupWindow.setWidth(widths);//ListPopupWindow.MATCH_PARENT还是会有边距，直接拿到屏幕宽度来设置也不行，因为默认的background有左右padding值。
  /*  int height = wm.getDefaultDisplay().getHeight();
    listPopupWindow.setHeight((int) (height *0.7));*/
    listPopupWindow.setAnchorView(btSwitchDirectory);
    listPopupWindow.setAdapter(listAdapter);
    listPopupWindow.setModal(true);

    listPopupWindow.setDropDownGravity(Gravity.BOTTOM);
    listPopupWindow.setAnimationStyle(R.style.__picker_mystyle);
    //listPopupWindow.getAnchorView().setMinimumHeight((int) (0.7* getResources().getDisplayMetrics().heightPixels));

    listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listPopupWindow.dismiss();

        PhotoDirectory directory = directories.get(position);

        btSwitchDirectory.setText(directory.getName().toLowerCase());//默认会大写，这里要改成小写

        photoGridAdapter.setCurrentDirectoryIndex(position);
        photoGridAdapter.notifyDataSetChanged();
      }
    });

    photoGridAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
      @Override public void onClick(View v, int position, boolean showCamera) {
        final int index = showCamera ? position - 1 : position;

        List<String> photos = photoGridAdapter.getCurrentPhotoPaths();

        int[] screenLocation = new int[2];
        v.getLocationOnScreen(screenLocation);

        //todo 怎么将相关的数据传递出去?

        /*ImagePagerFragment imagePagerFragment =
            ImagePagerFragment.newInstance(photos, index, screenLocation, v.getWidth(),
                v.getHeight());*/



        ((PhotoPickerActivity) getActivity()).showPagerFragment(photos, index, screenLocation, v.getWidth(),
                v.getHeight());

      }
    });

    photoGridAdapter.setOnCameraClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        try {
          Intent intent = captureManager.dispatchTakePictureIntent();
          startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

    btSwitchDirectory.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {

        if (listPopupWindow.isShowing()) {
          listPopupWindow.dismiss();
        } else if (!getActivity().isFinishing()) {
          adjustHeight();
          listPopupWindow.show();
          listPopupWindow.getListView().setVerticalScrollBarEnabled(false);
          listPopupWindow.getAnchorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
              //listPopupWindow.getAnchorView().setMinimumHeight((int) (0.7* getResources().getDisplayMetrics().heightPixels));
              listPopupWindow.getAnchorView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
          });

          //去掉滑动条,listview 在show之后才建立，所以需要该方法在show之后调用，否则会空指针
        }
      }
    });


    //预览按钮
    btnPreview.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
       if (photoGridAdapter.getSelectedPhotoPaths().size() > 0){
         PhotoPreview.builder()
                 .setPhotos(photoGridAdapter.getSelectedPhotoPaths())
                 .setCurrentItem(0)
                 .start(getActivity());
       }else {
         Toast.makeText(getActivity(),"还没有选择图片",Toast.LENGTH_SHORT).show();
       }
      }
    });


    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // Log.d(">>> Picker >>>", "dy = " + dy);
       /* if (Math.abs(dy) > SCROLL_THRESHOLD) {
          //mGlideRequestManager.pauseRequests();
          //ImageLoader.
          GlobalConfig.getLoader().pause();
        } else {
          //mGlideRequestManager.resumeRequests();
          GlobalConfig.getLoader().resume();
        }*/
      }
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          //mGlideRequestManager.resumeRequests();
          GlobalConfig.getLoader().resume();
        }else {
          GlobalConfig.getLoader().pause();
        }
      }
    });

    return rootView;
  }


  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
      captureManager.galleryAddPic();
      if (directories.size() > 0) {
        String path = captureManager.getCurrentPhotoPath();
        PhotoDirectory directory = directories.get(INDEX_ALL_PHOTOS);
        directory.getPhotos().add(INDEX_ALL_PHOTOS, new Photo(path.hashCode(), path));
        directory.setCoverPath(path);
        photoGridAdapter.notifyDataSetChanged();
      }
    }
  }


  public PhotoGridAdapter getPhotoGridAdapter() {
    return photoGridAdapter;
  }


  @Override public void onSaveInstanceState(Bundle outState) {
    captureManager.onSaveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }


  @Override public void onViewStateRestored(Bundle savedInstanceState) {
    captureManager.onRestoreInstanceState(savedInstanceState);
    super.onViewStateRestored(savedInstanceState);
  }

  public ArrayList<String> getSelectedPhotoPaths() {
    return photoGridAdapter.getSelectedPhotoPaths();
  }

  public void adjustHeight() {
    if (listAdapter == null) return;
    int count = listAdapter.getCount();
    count = count < COUNT_MAX ? count : COUNT_MAX;
    if (listPopupWindow != null) {
      listPopupWindow.setHeight(count * getResources().getDimensionPixelOffset(R.dimen.__picker_item_directory_height));
    }
  }

    @Override
    public void onDestroyView() {
        ImageLoader.getActualLoader().clearMomoryCache(getView());
        super.onDestroyView();
      Log.e("fragment","onDestroyView----picker");

    }

  @Override
  public void onStop() {
    super.onStop();
    Log.e("fragment","onStop----picker");
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Log.e("fragment","onDestroy----picker");
   // ImageLoader.getActualLoader().clearMomoryCache(getView());
    //Glide.get(this).clearMemory();
    if (directories == null) {
      return;
    }

    for (PhotoDirectory directory : directories) {
      directory.getPhotoPaths().clear();
      directory.getPhotos().clear();
      directory.setPhotos(null);
    }
    directories.clear();
    directories = null;
  }
}
