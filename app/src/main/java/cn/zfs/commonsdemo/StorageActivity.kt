package cn.zfs.commonsdemo

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.snail.commons.entity.Storage
import com.snail.commons.utils.FileUtils
import com.snail.commons.utils.SystemUtils
import com.snail.widget.listview.BaseListAdapter
import com.snail.widget.listview.BaseViewHolder
import kotlinx.android.synthetic.main.activity_storage.*


/**
 * 描述:
 * 时间: 2018/5/27 14:20
 * 作者: zengfansheng
 */
class StorageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)
        val storages = SystemUtils.getStorages(this)
        if (storages != null) {
            lv.adapter = ListAdapter(this, storages)
        }
    }

    private inner class ListAdapter(context: Context, data: MutableList<Storage>) : BaseListAdapter<Storage>(context, data) {
        override fun createViewHolder(position: Int): BaseViewHolder<Storage> {
            return object : BaseViewHolder<Storage>() {
                private var tvPath: TextView? = null
                private var tvAvailaleSize: TextView? = null
                private var tvTotalSize: TextView? = null
                private var tvRemovable: TextView? = null
                private var tvAllowMassStorage: TextView? = null
                private var tvUsb: TextView? = null
                private var tvPrimary: TextView? = null
                private var tvDesc: TextView? = null
                private var tvState: TextView? = null

                override fun createConvertView(): View {
                    val view = View.inflate(context, R.layout.item_storage, null)
                    tvPath = view.findViewById(R.id.tvPath)
                    tvAvailaleSize = view.findViewById(R.id.tvAvailaleSize)
                    tvTotalSize = view.findViewById(R.id.tvTotalSize)
                    tvRemovable = view.findViewById(R.id.tvRemovable)
                    tvState = view.findViewById(R.id.tvState)
                    tvAllowMassStorage = view.findViewById(R.id.tvAllowMassStorage)
                    tvUsb = view.findViewById(R.id.tvUsb)
                    tvDesc = view.findViewById(R.id.tvDesc)
                    tvPrimary = view.findViewById(R.id.tvPrimary)
                    return view
                }

                override fun onBind(item: Storage, position: Int) {
                    tvPath!!.text = item.path
                    tvDesc!!.text = item.description
                    tvAvailaleSize!!.text = FileUtils.formatFileSize(item.availaleSize)
                    tvTotalSize!!.text = FileUtils.formatFileSize(item.totalSize)
                    tvRemovable!!.text = item.isRemovable.toString()
                    tvPrimary!!.text = item.isPrimary.toString()
                    tvAllowMassStorage!!.text = item.isAllowMassStorage.toString()
                    tvUsb!!.text = item.isUsb.toString()
                    tvState!!.text = item.state
                }
            }
        }
    }
}