/*
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.litho;

import static android.support.v4.view.ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO;
import static android.support.v4.view.ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO;
import static android.support.v4.view.ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES;
import static org.assertj.core.api.Java6Assertions.assertThat;

import android.util.SparseArray;
import android.view.View;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

/**
 * Tests {@link MountItem}
 */

@RunWith(ComponentsTestRunner.class)
public class MountItemTest {
  private MountItem mMountItem;
  private Component mComponent;
  private ComponentHost mComponentHost;
  private Object mContent;
  private CharSequence mContentDescription;
  private Object mViewTag;
  private SparseArray<Object> mViewTags;
  private EventHandler mClickHandler;
  private EventHandler mLongClickHandler;
  private EventHandler mFocusChangeHandler;
  private EventHandler mTouchHandler;
  private EventHandler mDispatchPopulateAccessibilityEventHandler;
  private int mFlags;
  private ComponentContext mContext;
  private NodeInfo mNodeInfo;

  @Before
  public void setup() throws Exception {
    mContext = new ComponentContext(RuntimeEnvironment.application);
    mMountItem = new MountItem();

    mComponent =
        new InlineLayoutSpec() {
          @Override
          protected Component onCreateLayout(ComponentContext c) {
            return TestDrawableComponent.create(c).build();
          }
        };
    mComponentHost = new ComponentHost(RuntimeEnvironment.application);
    mContent = new View(RuntimeEnvironment.application);
    mContentDescription = "contentDescription";
    mViewTag = "tag";
    mViewTags = new SparseArray<>();
    mClickHandler = new EventHandler(mComponent, 5);
    mLongClickHandler = new EventHandler(mComponent, 3);
    mFocusChangeHandler = new EventHandler(mComponent, 9);
    mTouchHandler = new EventHandler(mComponent, 1);
    mDispatchPopulateAccessibilityEventHandler = new EventHandler(mComponent, 7);
    mFlags = 114;

    mNodeInfo = NodeInfo.acquire();
    mNodeInfo.setContentDescription(mContentDescription);
    mNodeInfo.setClickHandler(mClickHandler);
    mNodeInfo.setLongClickHandler(mLongClickHandler);
    mNodeInfo.setFocusChangeHandler(mFocusChangeHandler);
    mNodeInfo.setTouchHandler(mTouchHandler);
    mNodeInfo.setViewTag(mViewTag);
    mNodeInfo.setViewTags(mViewTags);

    mMountItem.init(
        mComponent,
        mComponentHost,
        mContent,
        mNodeInfo,
        null,
        null,
        mFlags,
        IMPORTANT_FOR_ACCESSIBILITY_YES,
        null);
  }

  @Test
  public void testIsBound() {
    mMountItem.setIsBound(true);
    assertThat(mMountItem.isBound()).isTrue();

    mMountItem.setIsBound(false);
    assertThat(mMountItem.isBound()).isFalse();
  }

  @Test
  public void testGetters() {
    assertThat(mMountItem.getComponent()).isSameAs((Component) mComponent);
    assertThat(mMountItem.getHost()).isSameAs(mComponentHost);
    assertThat(mMountItem.getContent()).isSameAs(mContent);
    assertThat(mMountItem.getNodeInfo().getContentDescription()).isSameAs(mContentDescription);
    assertThat(mMountItem.getNodeInfo().getClickHandler()).isSameAs(mClickHandler);
    assertThat(mMountItem.getNodeInfo().getFocusChangeHandler()).isSameAs(mFocusChangeHandler);
    assertThat(mMountItem.getNodeInfo().getTouchHandler()).isSameAs(mTouchHandler);
    assertThat(mMountItem.getFlags()).isEqualTo(mFlags);
    assertThat(mMountItem.getImportantForAccessibility())
        .isEqualTo(IMPORTANT_FOR_ACCESSIBILITY_YES);
  }

  @Test
  public void testFlags() {
    mFlags =
        MountItem.FLAG_DUPLICATE_PARENT_STATE;
    assertThat(MountItem.isDuplicateParentState(mFlags)).isTrue();
  }

  @Test
  public void testIsAccessibleWithNullComponent() {
    final MountItem mountItem = new MountItem();
    mountItem.init(
        null,
        mComponentHost,
        mContent,
        mNodeInfo,
        null,
        null,
        mFlags,
        IMPORTANT_FOR_ACCESSIBILITY_AUTO,
        null);

    assertThat(mountItem.isAccessible()).isFalse();
  }

  @Test
  public void testIsAccessibleWithAccessibleComponent() {
    final MountItem mountItem = new MountItem();

    mountItem.init(
        TestDrawableComponent.create(
                mContext, true, true, true, true, /* implementsAccessibility */ false)
            .build(),
        mComponentHost,
        mContent,
        mNodeInfo,
        null,
        null,
        mFlags,
        IMPORTANT_FOR_ACCESSIBILITY_AUTO,
        null);

    assertThat(mountItem.isAccessible()).isTrue();
  }

  @Test
  public void testIsAccessibleWithDisabledAccessibleComponent() {
    final MountItem mountItem = new MountItem();
    mountItem.init(
        TestDrawableComponent.create(
                mContext, true, true, true, true, /* implementsAccessibility */ false)
            .build(),
        mComponentHost,
        mContent,
        mNodeInfo,
        null,
        null,
        mFlags,
        IMPORTANT_FOR_ACCESSIBILITY_NO,
        null);

    assertThat(mountItem.isAccessible()).isFalse();
  }

  @Test
  public void testIsAccessibleWithAccessibilityEventHandler() {
    final MountItem mountItem = new MountItem();
    mountItem.init(
        TestDrawableComponent.create(
                mContext, true, true, true, true, /* implementsAccessibility */ false)
            .build(),
        mComponentHost,
        mContent,
        mNodeInfo,
        null,
        null,
        mFlags,
        IMPORTANT_FOR_ACCESSIBILITY_AUTO,
        null);

    assertThat(mountItem.isAccessible()).isTrue();
  }

  @Test
  public void testIsAccessibleWithNonAccessibleComponent() {
    assertThat(mMountItem.isAccessible()).isFalse();
  }

  @Test
  public void testRelease() {
    mMountItem.release(new ComponentContext(RuntimeEnvironment.application));
    assertThat(mMountItem.getComponent()).isNull();
    assertThat(mMountItem.getHost()).isNull();
    assertThat(mMountItem.getContent()).isNull();
    assertThat(mMountItem.getNodeInfo()).isNull();
    assertThat(mMountItem.getFlags()).isEqualTo(0);
    assertThat(mMountItem.getImportantForAccessibility())
        .isEqualTo(IMPORTANT_FOR_ACCESSIBILITY_AUTO);
  }
}
