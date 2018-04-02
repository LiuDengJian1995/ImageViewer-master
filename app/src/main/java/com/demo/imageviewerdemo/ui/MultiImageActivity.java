package com.demo.imageviewerdemo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.demo.imageviewerdemo.MainActivity;
import com.demo.imageviewerdemo.R;
import com.demo.imageviewerdemo.view.SpaceDecoration;
import com.liudengjian.imageviewer.ImageViewer;
import com.liudengjian.imageviewer.anim.CubeTransformer;
import com.liudengjian.imageviewer.anim.DepthPageTransformer;
import com.liudengjian.imageviewer.anim.GalleryTransformer;
import com.liudengjian.imageviewer.anim.RotateDownPageTransformer;
import com.liudengjian.imageviewer.anim.ViewpagerTransformAnim;
import com.liudengjian.imageviewer.anim.ZoomOutPageTransformer;
import com.liudengjian.imageviewer.listener.SourceImageViewGet;
import com.liudengjian.imageviewer.util.ImageViewerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 * 多张图片 滑动动画使用ImageViewer类中的setPageTransformer方法
 * 动画需要继承ViewPager.PageTransformer
 * 已经实现CubeTransformer、DepthPageTransformer、GalleryTransformer、RotateDownPageTransformer、ViewpagerTransformAnim、ZoomOutPageTransformer
 */
public class MultiImageActivity extends AppCompatActivity {
    public static String[] netImages = {
            "http://wx1.sinaimg.cn/bmiddle/9672f95cly1fgcl0xc7hmj20gt5fwhdt.jpg",
            "http://imgsrc.baidu.com/forum/pic/item/fe4d309b033b5bb51ee189753cd3d539b700bc85.jpg",
            "http://imgsrc.baidu.com/forum/pic/item/27e9c23f8794a4c2b90fd32708f41bd5ad6e396b.jpg",
            "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2966021298,3341101515&fm=23&gp=0.jpg",
            "http://imgsrc.baidu.com/forum/pic/item/c8baa0fd5266d01673d7fc24912bd40734fa3555.jpg",
            "https://gss0.baidu.com/9fo3dSag_xI4khGko9WTAnF6hhy/zhidao/wh%3D600%2C800/sign=0b1c8a225c4e9258a6618ee8acb2fd60/c75c10385343fbf2b02ec465ba7eca8065388f71.jpg",
            "http://imgsrc.baidu.com/forum/pic/item/71ffcd177f3e67090093617a31c79f3df9dc5519.jpg"
    };
    private ViewPager.PageTransformer transformer = null;

    private RecyclerView recyclerView;

    private PhotoAlbumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_image);

        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new SpaceDecoration(10));

        adapter = new PhotoAlbumAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setData(Arrays.asList(netImages));
        adapter.notifyDataSetChanged();
    }


    class PhotoAlbumAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

        private Context context;
        private List<String> images = new ArrayList<>();
        private int itemSize = 100;

        PhotoAlbumAdapter(Context context) {
            this.context = context;
            ImageViewerUtil.init(context);
            itemSize = (ImageViewerUtil.getScreenWidth() - 10 * 4) / 3;
        }

        public void setData(List<String> images) {
            this.images = images;
        }

        @Override
        public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());

            imageView.setLayoutParams(new ViewGroup.LayoutParams(itemSize, itemSize));
            return new PhotoViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
            Glide.with(context).load(images.get(position))
                    .placeholder(R.drawable.place_holder)
                    .into((ImageView) holder.itemView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageViewer.with(context)
                            .setImageList(images)
                            .setSourceImageView(new SourceImageViewGet() {
                                @Override
                                public ImageView getImageView(int pos) {
                                    int layoutPos = recyclerView.indexOfChild(holder.itemView);
                                    View view = recyclerView.getChildAt(layoutPos + pos - position);
                                    if (view != null) return (ImageView) view;
                                    return (ImageView) holder.itemView;
                                }
                            })
                            .setNowIndex(position)
                            .setPageTransformer(transformer)
                            .setShowMore(true)
                            .show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return images.size();
        }
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        private SparseArray<View> views;

        public PhotoViewHolder(View itemView) {
            super(itemView);
        }

        public <E extends View> E get(int id) {
            View childView = views.get(id);
            if (null == childView) {
                childView = itemView.findViewById(id);
                views.put(id, childView);
            }
            return (E) childView;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_null: {
                transformer = null;
                break;
            }
            case R.id.action_cube: {
                transformer = new CubeTransformer();
                break;
            }
            case R.id.action_depthPage: {
                transformer = new DepthPageTransformer();
                break;
            }
            case R.id.action_gallery:
                transformer = new GalleryTransformer();
                break;
            case R.id.action_rotateDownPage:
                transformer = new RotateDownPageTransformer();
                break;
            case R.id.action_viewpager:
                transformer = new ViewpagerTransformAnim();
                break;
            case R.id.action_zoomOutPage:
                transformer = new ZoomOutPageTransformer();
                break;
        }
        return true;
    }
}
