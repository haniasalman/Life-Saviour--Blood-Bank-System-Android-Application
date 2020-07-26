package com.example.lifesaviour


import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
import java.util.ArrayList


class RequestBloodList : AppCompatActivity() {

    lateinit var recycler_View: RecyclerView


    var total_item = 0
    var last_variabe_item = 0
    val item_count = 100

    lateinit var adapter: RequestBloodrecyclerviewAdapter

    var isLoading = false
    var isMaxData = false

    var last_node: String? = ""
    var last_key: String? = ""

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.refresh_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == R.id.refresh) {
            isMaxData = false
            last_node = adapter.lastItemId
            adapter.notifyDataSetChanged()
            getUsers()
            getLastKey()

        }
        return true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_blood_list)

        getLastKey()

        val layoutManager = LinearLayoutManager(this)
                recycler_View = findViewById(R.id.recyclerView)
        recycler_View.layoutManager = layoutManager
        adapter = RequestBloodrecyclerviewAdapter(this)   //////
        recycler_View.adapter = adapter

        getUsers()

        recycler_View.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                total_item = layoutManager.itemCount
                last_variabe_item = layoutManager.findLastVisibleItemPosition()

                if(!isLoading && total_item <= last_variabe_item+ item_count){
                    getUsers()
                    isLoading = true
                }
            }
        })

    }

    private fun getUsers() {
        if (!isMaxData) {
            val query: Query
            if (TextUtils.isEmpty(last_node))
                query = FirebaseDatabase.getInstance().reference
                    .child("Request Blood")
                    .orderByKey()
                    .limitToFirst(item_count)
            else
                query = FirebaseDatabase.getInstance().reference
                    .child("Request Blood")
                    .orderByKey()
                    .startAt(last_node)
                    .limitToFirst(item_count)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        val postList = ArrayList<requestDataModel>()
                        for (snapshot in p0.children)
                            postList.add(snapshot.getValue(requestDataModel::class.java)!!)

                        last_node = postList[postList.size - 1].BloodUnitsID
                        if(last_node.equals(last_key))
                            postList.removeAt(postList.size-1)
                        else
                            last_node = "end"

                        postList.reverse()  //to show requests in descending order
                        adapter.addAll(postList)
                        isLoading = false

                    } else {
                        isLoading = false
                        isMaxData = true
                    }

                }
            })
        }
    }

        fun getLastKey() {

            val get_last_key = FirebaseDatabase.getInstance().reference
                .child("Request Blood")
                .orderByKey()
                .limitToLast(1)
            get_last_key.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (userSnapshot in p0.children)
                        last_key = userSnapshot.key
                }

            })
        }
}




