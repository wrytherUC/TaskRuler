package com.taskruler

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.auth.GoogleAuthException
import com.google.api.Distribution.BucketOptions.Exponential
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.CalendarScopes

import java.security.AccessController.getContext

import java.util.Calendar



class GetEventFragment {
    private var mCredential: GoogleAccountCredential? = null
    private var mService: Calendar? = null

    private fun initCredentials(){
        mCredential = GoogleAccountCredential.usingOAuth2(
            requireContext(),
            arrayListOf(CalendarScopes.CALENDAR)
        )
            .setBackOff(ExponentialBackOff())
        initCalendarBuild(mCredential)
    }
    private fun initCalendarBuild(credential: GoogleAccountCredential?){
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        mService = Calendar.Builder(
            transport, jsonFactory, credential
        )
            .setApplicationName("GetEvenCalendar")
            .build()
    }
}