package com.chinadci.mel.mleo.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.TrackAnnexesTable;
import com.chinadci.mel.mleo.ldb.TrackCaseTable;
import com.chinadci.mel.mleo.ldb.TrackInspectionTable;
import com.chinadci.mel.mleo.ldb.TrackPatrolTable;
import com.chinadci.mel.mleo.ui.views.CaseView;
import com.chinadci.mel.mleo.ui.views.HisPatrolViewGroup;
import com.chinadci.mel.mleo.ui.views.InspectionEditeView;
import com.chinadci.mel.mleo.ui.views.InspectionView;
import com.chinadci.mel.mleo.ui.views.PatrolView;
/**
 * 
* @ClassName LandCaseTrackFragment 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:46:51 
*
 */
@SuppressLint("SimpleDateFormat")
public class LandCaseTrackFragment extends LandCaseViewpagerFragment {
	String caseId;
	CaseView caseInfoView;
	InspectionView inspectionView;
	InspectionEditeView inspectionEditeView;
	PatrolView patrolView;
	HisPatrolViewGroup hisPatrolView;
	AlertDialog alertDialog;

	@Override
	public void refreshUi() {
		// TODO Auto-generated method stub
		super.refreshUi();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		caseId = getArguments().getString(Parameters.CASE_ID);
		contentView = inflater.inflate(R.layout.fragment_case_viewpager,
				container, false);
		initViewpager();
		initFragment();
		return contentView;
	}

	void initFragment() {
		try {
			// 信息核查
			inspectionView = new InspectionView(context);
			inspectionView.setDataSource(caseId, TrackInspectionTable.name);
			viewList.add(inspectionView);

			// 处理结果
			hisPatrolView = new HisPatrolViewGroup(context);
			hisPatrolView.setParentActivity(getActivity());
			hisPatrolView.setDataSource(currentUser, caseId,
					TrackPatrolTable.name, TrackAnnexesTable.name);
			viewList.add(hisPatrolView);

			// 上报信息
			caseInfoView = new CaseView(context);
			caseInfoView.setParentActivity(getActivity());
			caseInfoView.setDataSource(caseId, TrackCaseTable.name,
					TrackAnnexesTable.name);
			viewList.add(caseInfoView);

			pagerAdapter = new ViewPagerAdapter(viewList, titleList);
			viewPager.setAdapter(pagerAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
