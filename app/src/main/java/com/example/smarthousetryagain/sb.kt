package com.example.smarthousetryagain

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object sb {
    val client = createSupabaseClient(
        supabaseUrl = "https://ihyknrqszskicibjrtiv.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImloeWtucnFzenNraWNpYmpydGl2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzIyMTMxNjMsImV4cCI6MjA0Nzc4OTE2M30.fTYsD-bhpuEDLCNwfynB6YpBHpY9G9E164UoBRWEdAw"
    ) {
        install(GoTrue)
        install(Postgrest)
        install(Storage)
    }

    public fun getSB(): SupabaseClient {
        return client;
    }
}