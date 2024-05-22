package com.houvven.guise.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.annotation.StringRes
import com.houvven.guise.R
import com.topjohnwu.superuser.ipc.RootService
import io.github.houvven.lservice.ILServiceBridge
import io.github.houvven.lservice.LServiceBridgeRootService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.lsposed.lspd.ILSPManagerService
import org.lsposed.lspd.service.ILSPApplicationService
import kotlin.concurrent.thread

/**
 * This object is a client for the LService.
 * It provides methods to start the service, handle its connection, and interact with the service.
 */
object LServiceBridgeClient {

    private lateinit var lserviceBridge: ILServiceBridge

    private val _statusFlow: MutableStateFlow<Status> = MutableStateFlow(Status.Connecting)
    val statusFlow = _statusFlow.asStateFlow()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i(TAG, "onServiceConnected: $name")
            lserviceBridge = ILServiceBridge.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG, "onServiceDisconnected: $name")
            _statusFlow.update { Status.Disconnected }
        }

        override fun onBindingDied(name: ComponentName?) {
            super.onBindingDied(name)
            Log.i(TAG, "onBindingDied: $name")
            _statusFlow.update { Status.BindingDied }
        }
    }

    // The application service of the LService
    private val applicationService: ILSPApplicationService
        get() = ILSPApplicationService.Stub.asInterface(lserviceBridge.applicationServiceBinder)

    /**
     * Starts the LService and binds to it.
     * @param context The context used to start the service.
     */

    fun start(context: Context) {
        val intent = Intent(context, LServiceBridgeRootService::class.java)
        intent.putExtra(LServiceBridgeRootService.MANAGER_APK_PATH, context.managerApkPath)
        try {
            Runtime.getRuntime().exec("su")
        } catch (e: Exception) {
            _statusFlow.update { Status.Error.RootRequired }
            return
        }
        RootService.bind(intent, connection)

        thread { // watch the connection status
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 5000) {
                if (::lserviceBridge.isInitialized) {
                    Log.i(TAG, "LService bridge connected")
                    _statusFlow.update { Status.Connected }
                    break
                }
                kotlin.runCatching { Thread.sleep(200) }
            }
        }
    }

    /**
     * Gets the manager service of the LService.
     * @param applicationService The application service of the LService. Defaults to this object's application service.
     * @return The manager service of the LService.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getManagerService(applicationService: ILSPApplicationService = this.applicationService): ILSPManagerService {
        val binder = lserviceBridge.getManagerServiceBinder(applicationService)
        return ILSPManagerService.Stub.asInterface(binder)
    }

    /**
     * Gets the path of the manager APK.
     * If the APK does not exist, it is created from the assets.
     */
    private val Context.managerApkPath: String
        get() {
            val relativePath = "lservice/manager.apk"
            val managerFile = cacheDir.resolve(relativePath)
            if (!managerFile.exists()) {
                managerFile.parentFile?.mkdirs()
                assets.open(relativePath).use { input ->
                    managerFile.outputStream().use { output -> input.copyTo(output) }
                }
            }
            return managerFile.absolutePath
        }

    // The tag used for logging
    private const val TAG = "LService"


    /** A sealed interface representing the various states of the LService connection. */
    sealed interface Status {
        /** Represents the state when the LService is successfully connected. */
        data object Connected : Status

        /** Represents the state when the LService is disconnected. */
        data object Disconnected : Status

        /** Represents the state when the LService is in the process of connecting. */
        data object Connecting : Status

        /** Represents the state when the binding to the LService has died. */
        data object BindingDied : Status

        /** A sealed interface representing the various error states of the LService connection. */
        sealed class Error(@StringRes val messageResId: Int) : Status {
            /** Represents the error state when root access is required. */
            data object RootRequired : Error(R.string.required_permissions_root)
        }
    }
}