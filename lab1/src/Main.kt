import java.io.File

fun main() {
    //тарифи
    val plans = listOf(
        Tariff("Basic", 10, 5.0, 2),
        Tariff("Premium", 100, 50.0, 20),
        Tariff("Turbo", 200, 100.0, 50),
        Tariff("Super Plus", 500, 200.0, 100),
        Tariff("Unlimited", null, null, null) // Безлімітний тариф
    )

    // зчитування з файлу
    val users = loadUsersFromFile(plans)

    // Окружність циклу для меню
    var continueRunning = true
    while (continueRunning) {
        println("\n--- Menu ---")
        println("1. View all users")
        println("2. Add new user")
        println("3. Edit user")
        println("4. Exit")
        print("Enter your choice: ")

        when (readLine()?.toInt()) {
            1 -> viewUsers(users)
            2 -> {
                val newUser = addNewUser(plans)
                users.add(newUser)
                saveUsersToFile(users)
                println("New user added successfully.")
            }
            3 -> {
                editUser(users, plans)
                saveUsersToFile(users)
                println("User data updated successfully.")
            }
            4 -> {
                println("Exiting program...")
                continueRunning = false
            }
            else -> println("Invalid choice. Please enter a valid option.")
        }
    }
}

fun viewUsers(users: List<User>) {
    if (users.isEmpty()) {
        println("No users found.")
    } else {
        println("\nAll users:")
        users.forEachIndexed { index, user ->
            println("${index + 1}. ${user.name} (${user.phoneNumber}) - Plan: ${user.tariff?.name}, Internet used: ${user.internetUsed} GB")
        }
    }
}

fun addNewUser(plans: List<Tariff>): User {
    println("Enter the name of the new user:")
    val name = readLine()!!

    println("Enter the phone number of the new user (e.g., +38 099 123 4567):")
    val phoneNumber = readLine()!!

    println("Enter the number of GB used by the user:")
    val internetUsed = readLine()!!.toDouble()

    println("Choose a tariff plan for the user:")
    plans.forEachIndexed { index, tariff ->
        println("${index + 1}. ${tariff.name}")
    }
    val tariffChoice = readLine()!!.toInt() - 1
    val selectedTariff = plans[tariffChoice]

    return User(name, selectedTariff, phoneNumber).apply { useInternet(internetUsed) }
}

fun editUser(users: MutableList<User>, plans: List<Tariff>) {
    println("Enter the number of the user to edit:")
    viewUsers(users)
    val userIndex = readLine()?.toIntOrNull()?.minus(1)

    if (userIndex != null && userIndex in users.indices) {
        val user = users[userIndex]
        println("Editing user: ${user.name}")

        println("Enter new phone number (current: ${user.phoneNumber}):")
        val newPhoneNumber = readLine()?.takeIf { it.isNotBlank() } ?: user.phoneNumber
        user.phoneNumber = newPhoneNumber

        println("Choose new tariff plan (current: ${user.tariff?.name}):")
        plans.forEachIndexed { index, tariff ->
            println("${index + 1}. ${tariff.name}")
        }
        val tariffChoice = readLine()?.toIntOrNull()?.minus(1)
        if (tariffChoice != null && tariffChoice in plans.indices) {
            user.tariff = plans[tariffChoice]
        }

        println("Enter new internet usage in GB (current: ${user.internetUsed} GB):")
        val newInternetUsed = readLine()?.toDoubleOrNull()
        if (newInternetUsed != null) {
            user.setInternetUsed(newInternetUsed)
        }
    } else {
        println("Invalid user number.")
    }
}

fun saveUsersToFile(users: List<User>) {
    val file = File("users.txt")
    file.printWriter().use { out ->
        users.forEach { user ->
            out.println("${user.name}|${user.phoneNumber}|${user.tariff?.name}|${user.internetUsed}")
        }
    }
}

fun loadUsersFromFile(plans: List<Tariff>): MutableList<User> {
    val file = File("users.txt")
    val users = mutableListOf<User>()

    if (file.exists()) {
        file.forEachLine { line ->
            val parts = line.split("|")
            if (parts.size == 4) {
                val name = parts[0]
                val phoneNumber = parts[1]
                val tariffName = parts[2]
                val internetUsed = parts[3].toDouble()

                val tariff = plans.firstOrNull { it.name == tariffName } ?: plans[0] 
                val user = User(name, tariff, phoneNumber).apply { useInternet(internetUsed) }
                users.add(user)
            }
        }
    }
    return users
}

data class Tariff(
    val name: String,
    val minutes: Int?,
    val internet: Double?,
    val sms: Int?
)

class User(var name: String, var tariff: Tariff?, var phoneNumber: String) {
    private var _internetUsed: Double = 0.0
    val internetUsed: Double get() = _internetUsed

    fun useInternet(amount: Double) {
        if (tariff?.internet != null && _internetUsed + amount > tariff!!.internet!!) {
            println("$name has exceeded the internet limit!")
        } else {
            _internetUsed += amount
        }
    }

    fun setInternetUsed(amount: Double) {
        _internetUsed = amount
    }
}
