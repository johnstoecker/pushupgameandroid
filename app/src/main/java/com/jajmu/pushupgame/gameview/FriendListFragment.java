package com.jajmu.pushupgame.gameview;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.jajmu.pushupgame.PushUpApplication;
import com.jajmu.pushupgame.R;
import com.jajmu.pushupgame.facebook.FriendListElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FriendListFragment extends Fragment {
    private ListView listView;
    private List<FriendListElement> listElements;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendListFragment newInstance(String param1, String param2) {
        FriendListFragment fragment = new FriendListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public FriendListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend_list, container, false);

        // Find the list view
        listView = (ListView) v.findViewById(R.id.selection_list);
        // Set up the list view items, based on a list of
        // BaseListElement items
        listElements = new ArrayList<FriendListElement>();
        // Add an item for the friend picker
        listElements.add(new PeopleListElement(0));
        // Set the list view adapter
        listView.setAdapter(new ActionListAdapter(getActivity(),
                R.id.selection_list, listElements));

        // Check for an open session
        Session session = Session.getActiveSession();
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFriendSelect(List<GraphUser> opponents);
    }

    private class ActionListAdapter extends ArrayAdapter<FriendListElement> {
        private List<FriendListElement> listElements;

        public ActionListAdapter(Context context, int resourceId,
                                 List<FriendListElement> listElements) {
            super(context, resourceId, listElements);
            this.listElements = listElements;
            // Set up as an observer for list item changes to
            // refresh the view.
            for (int i = 0; i < listElements.size(); i++) {
                listElements.get(i).setAdapter(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater =
                        (LayoutInflater) getActivity()
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.friend_list_item, null);
            }

            FriendListElement listElement = listElements.get(position);
            if (listElement != null) {
                view.setOnClickListener(listElement.getOnClickListener());
                ImageView icon = (ImageView) view.findViewById(R.id.icon);
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                TextView text2 = (TextView) view.findViewById(R.id.text2);
                if (icon != null) {
                    icon.setImageDrawable(listElement.getIcon());
                }
                if (text1 != null) {
                    text1.setText(listElement.getText1());
                }
                if (text2 != null) {
                    text2.setText(listElement.getText2());
                }
            }
            return view;
        }

    }

    private class PeopleListElement extends FriendListElement {
        private List<GraphUser> selectedUsers;
        private static final String FRIENDS_KEY = "friends";

        private byte[] getByteArray(List<GraphUser> users) {
            // convert the list of GraphUsers to a list of String
            // where each element is the JSON representation of the
            // GraphUser so it can be stored in a Bundle
            List<String> usersAsString = new ArrayList<String>(users.size());

            for (GraphUser user : users) {
                usersAsString.add(user.getInnerJSONObject().toString());
            }
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                new ObjectOutputStream(outputStream).writeObject(usersAsString);
                return outputStream.toByteArray();
            } catch (IOException e) {
                Log.e("FB", "Unable to serialize users.", e);
            }
            return null;
        }

        @Override
        protected void onSaveInstanceState(Bundle bundle) {
            if (selectedUsers != null) {
                bundle.putByteArray(FRIENDS_KEY,
                        getByteArray(selectedUsers));
            }
        }

        @Override
        public void onActivityResult(Intent data) {
            List<String> tmpList = data.getStringArrayListExtra("opponents");
            List<GraphUser> list = new ArrayList<GraphUser>();
            for (String tmpString : tmpList) {
                try {
                    GraphUser user = GraphObject.Factory.create(new JSONObject(tmpString), GraphUser.class);
                    list.add(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            selectedUsers = list;
            ((GameActivity)getActivity()).getGameStateManager()
                    .setOpponents(list);
            super.onActivityResult(data);
            setUsersText();
            notifyDataChanged();
        }

        public PeopleListElement(int requestCode) {
            super(getActivity().getResources().getDrawable(R.drawable.ic_launcher),
                    getActivity().getResources().getString(R.string.action_people),
                    getActivity().getResources().getString(R.string.action_people_default),
                    requestCode);
        }

        @Override
        public View.OnClickListener getOnClickListener() {
            return new View.OnClickListener() {
                public void onClick(View view) {
                    startPickerActivity(PickerActivity.FRIEND_PICKER, getRequestCode());
                }
            };
        }
        private void setUsersText() {
            System.out.println("setting users text");
            String text = null;
            if (selectedUsers != null) {
                // If there is one friend
                if (selectedUsers.size() == 1) {
                    text = String.format(getResources()
                                    .getString(R.string.single_user_selected),
                            selectedUsers.get(0).getName());
                } else if (selectedUsers.size() == 2) {
                    // If there are two friends
                    text = String.format(getResources()
                                    .getString(R.string.two_users_selected),
                            selectedUsers.get(0).getName(),
                            selectedUsers.get(1).getName());
                } else if (selectedUsers.size() > 2) {
                    // If there are more than two friends
                    text = String.format(getResources()
                                    .getString(R.string.multiple_users_selected),
                            selectedUsers.get(0).getName(),
                            (selectedUsers.size() - 1));
                }
            }
            if (text == null) {
                // If no text, use the placeholder text
                text = getResources()
                        .getString(R.string.action_people_default);
            }
            // Set the text in list element. This will notify the
            // adapter that the data has changed to
            // refresh the list view.
//        setText2(text);
        }


    }
    private void startPickerActivity(Uri data, int requestCode) {
        Intent intent = new Intent();
        intent.setData(data);
        intent.setClass(getActivity(), PickerActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<String> tmpList = data.getStringArrayListExtra("opponents");
        List<GraphUser> list = new ArrayList<GraphUser>();
        for (String tmpString : tmpList) {
            try {
                GraphUser user = GraphObject.Factory.create(new JSONObject(tmpString), GraphUser.class);
                list.add(user);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mListener.onFriendSelect(list);
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
        System.out.println("inside friend list fragment");
        System.out.println(data);
    }


}
