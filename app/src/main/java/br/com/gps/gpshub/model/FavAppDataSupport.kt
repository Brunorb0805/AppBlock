package br.com.gps.gpshub.model

import org.litepal.crud.DataSupport


class FavAppDataSupport(var packageName: String, var isLocalApp: Boolean, var icon: Int) : DataSupport()