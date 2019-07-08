package com.example.menu4;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "My Activity";
    private ArFragment arFragment;

    private ModelRenderable andyRenderable;
    private ModelRenderable tophatRenderable;
    private ViewRenderable menu1Renderable;

    private boolean hasPlacedMenu = false;
    private boolean hasFinishedLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment)
                getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

        // Build Renderables

        CompletableFuture<ModelRenderable> andyStage =
                ModelRenderable.builder().setSource(this, R.raw.andy).build();
        CompletableFuture<ModelRenderable> tophatStage =
                ModelRenderable.builder().setSource(this, R.raw.top_hat).build();
        CompletableFuture<ViewRenderable> menu1Stage =
                ViewRenderable.builder().setView(this, R.layout.menu1).build();

        CompletableFuture.allOf(
                andyStage,
                tophatStage,
                menu1Stage)
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
//                                DemoUtils.displayError(this, "Unable to load renderable", throwable);
                                return null;
                            }

                            try {
                                andyRenderable = andyStage.get();
                                tophatRenderable = tophatStage.get();
                                menu1Renderable = menu1Stage.get();

                                // edit renderables
                                tophatRenderable.setShadowCaster(false);
                                tophatRenderable.setShadowReceiver(false);
                                menu1Renderable.setShadowCaster(false);
                                menu1Renderable.setShadowReceiver(false);

                                // Everything finished loading successfully.
                                hasFinishedLoading = true;

                                Log.d("tag1", "Finished renderables");

                            } catch (InterruptedException | ExecutionException ex) {
//                                DemoUtils.displayError(this, "Unable to load renderable", ex);
                            }

                            return null;
                        });

//        ModelRenderable.builder()
//                .setSource(this, R.raw.andy)
//                .build()
//                .thenAccept(renderable -> andyRenderable = renderable)
//                .exceptionally(
//                        throwable -> {
//                            Log.e(TAG, "Unable to load Renderable.", throwable);
//                            return null;
//                        });

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
//                    if (andyRenderable == null) {
//                        return;
//                    }

                    if (!hasFinishedLoading) {
                        // We can't do anything yet.
                        return;
                    }
                    Log.d("tag1", "Tapped. Trying to place.");

                    if (!hasPlacedMenu && tryPlaceMenu(hitResult, plane, motionEvent)) {
                        Log.d("tag1", "Successfully placed menu.");
                        hasPlacedMenu = true;
                    }
                });
    }

    private boolean tryPlaceMenu(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        // Create the Anchor.
        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        // Create menu
        Node menus = createMenus();
        anchorNode.addChild(menus);
        return true;
    }

    private Node createMenus() {
        Node base = new Node();

        Node center = new Node();
        center.setParent(base);
        center.setLocalPosition(new Vector3(0.0f, 0.5f, -0.5f));

        Node obj1 = new Node();
        obj1.setParent(center);
        obj1.setRenderable(tophatRenderable);
        obj1.setLocalPosition(new Vector3(0.0f, 0.5f, 0.0f));
        obj1.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));
        // might use setLocalScale, or just use transformablenode for obj

        Node menu1 = new Node();
        menu1.setParent(center);
        menu1.setRenderable(menu1Renderable);
        menu1.setLocalPosition(new Vector3(0.0f, -0.3f, 0.0f));

        // listener to seekbar
//        View solarControlsView = menu1Renderable.getView();
        // find all elements by name
//        SeekBar orbitSpeedBar = solarControlsView.findViewById(R.id.orbitSpeedBar);
//        orbitSpeedBar.setProgress((int) (solarSettings.getOrbitSpeedMultiplier() * 10.0f));
//        orbitSpeedBar.setOnSeekBarChangeListener(
//                new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        float ratio = (float) progress / (float) orbitSpeedBar.getMax();
//                        solarSettings.setOrbitSpeedMultiplier(ratio * 10.0f);
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {}
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {}
//                });
//
//        // listener to 2nd seekbar
//        SeekBar rotationSpeedBar = solarControlsView.findViewById(R.id.rotationSpeedBar);
//        rotationSpeedBar.setProgress((int) (solarSettings.getRotationSpeedMultiplier() * 10.0f));
//        rotationSpeedBar.setOnSeekBarChangeListener(
//                new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        float ratio = (float) progress / (float) rotationSpeedBar.getMax();
//                        solarSettings.setRotationSpeedMultiplier(ratio * 10.0f);
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {}
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {}
//                });

        // Toggle the solar controls on and off by tapping the sun.
//        sunVisual.setOnTapListener(
//                (hitTestResult, motionEvent) -> solarControls.setEnabled(!solarControls.isEnabled()));

//        createPlanet("Mercury", sun, 0.4f, 47f, mercuryRenderable, 0.019f, 0.03f);

        return base;

    }



}

// Create the transformable andy and add it to the anchor.
//                    TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
//                    andy.setParent(anchorNode);
//                    andy.setRenderable(andyRenderable);
//                    andy.select();

//        AnchorNode anchorNode = new AnchorNode(anchor);
//        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
//        node.setRenderable(renderable);
//        node.setParent(anchorNode);
//        fragment.getArSceneView().getScene().addChild(anchorNode);

//        Session session = arFragment.getArSceneView().getSession();
//        float[] pos = { 0,0,-1 };
//        float[] rotation = {0,0,0,1};
//        Anchor anchor =  session.createAnchor(new Pose(pos, rotation));
//        AnchorNode anchorNode = new AnchorNode(anchor);
//        anchorNode.setRenderable(andyRenderable);
//        anchorNode.setParent(arFragment.getArSceneView().getScene());