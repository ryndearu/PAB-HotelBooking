package com.kel7.bookinghotel.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kel7.bookinghotel.ui.screen.auth.LoginScreen
import com.kel7.bookinghotel.ui.screen.auth.RegisterScreen
import com.kel7.bookinghotel.ui.screen.booking.BookingScreen
import com.kel7.bookinghotel.ui.screen.history.BookingHistoryScreen
import com.kel7.bookinghotel.ui.screen.home.HomeScreen
import com.kel7.bookinghotel.ui.screen.hotel.HotelDetailScreen
import com.kel7.bookinghotel.ui.screen.payment.PaymentScreen
import com.kel7.bookinghotel.ui.screen.profile.EditProfileScreen
import com.kel7.bookinghotel.ui.screen.profile.ProfileScreen
import com.kel7.bookinghotel.ui.screen.search.SearchScreen
import com.kel7.bookinghotel.ui.viewmodel.AuthViewModel
import com.kel7.bookinghotel.ui.viewmodel.BookingViewModel
import com.kel7.bookinghotel.ui.viewmodel.HotelViewModel
import com.kel7.bookinghotel.ui.viewmodel.ProfileViewModel

@Composable
fun HotelBookingNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    hotelViewModel: HotelViewModel = viewModel { HotelViewModel(authViewModel.sharedRepository) },
    bookingViewModel: BookingViewModel = viewModel { BookingViewModel(authViewModel.sharedRepository) },
    profileViewModel: ProfileViewModel = viewModel { ProfileViewModel(authViewModel.sharedRepository) }
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val bookings by bookingViewModel.bookings.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) HomeScreen else LoginScreen
    ) {
        // Authentication Screens
        composable<LoginScreen> {
            LoginScreen(
                onLoginClick = { email, password ->
                    authViewModel.login(email, password)
                },
                onRegisterClick = {
                    navController.navigate(RegisterScreen)
                },
                isLoading = authViewModel.uiState.isLoading,
                errorMessage = authViewModel.uiState.errorMessage
            )
            
            // Handle successful login
            LaunchedEffect(authViewModel.uiState.isSuccess) {
                if (authViewModel.uiState.isSuccess) {
                    authViewModel.resetSuccess()
                    navController.navigate(HomeScreen) {
                        popUpTo(LoginScreen) { inclusive = true }
                    }
                }
            }
        }

        composable<RegisterScreen> {
            RegisterScreen(
                onRegisterClick = { email, password, name ->
                    authViewModel.register(email, password, name)
                },
                onLoginClick = {
                    navController.navigate(LoginScreen) {
                        popUpTo(RegisterScreen) { inclusive = true }
                    }
                },
                isLoading = authViewModel.uiState.isLoading,
                errorMessage = authViewModel.uiState.errorMessage
            )

            // Handle successful registration - redirect to login page
            LaunchedEffect(authViewModel.uiState.isSuccess) {
                if (authViewModel.uiState.isSuccess) {
                    authViewModel.resetSuccess()
                    authViewModel.logout() // Logout after registration to ensure user goes to login
                    navController.navigate(LoginScreen) {
                        popUpTo(RegisterScreen) { inclusive = true }
                    }
                }
            }
        }

        // Main App Screens
        composable<HomeScreen> {
            HomeScreen(
                hotels = hotelViewModel.uiState.hotels,
                user = currentUser,
                onHotelClick = { hotelId ->
                    navController.navigate(HotelDetailScreen(hotelId))
                },
                onSearchClick = {
                    navController.navigate(SearchScreen)
                },
                onProfileClick = {
                    if (currentUser != null) {
                        navController.navigate(ProfileScreen)
                    } else {
                        navController.navigate(LoginScreen)
                    }
                },
                onHistoryClick = {
                    if (currentUser != null) {
                        navController.navigate(BookingHistoryScreen)
                    } else {
                        navController.navigate(LoginScreen)
                    }
                }
            )
        }

        composable<SearchScreen> {
            SearchScreen(
                searchQuery = hotelViewModel.uiState.searchQuery,
                hotels = hotelViewModel.uiState.filteredHotels,
                onSearchQueryChange = { query ->
                    hotelViewModel.searchHotels(query)
                },
                onHotelClick = { hotelId ->
                    navController.navigate(HotelDetailScreen(hotelId))
                },
                onFilterClick = {
                    // TODO: Implement filter dialog
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<HotelDetailScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<HotelDetailScreen>()
            val hotel = hotelViewModel.getHotelById(args.hotelId)
            
            hotel?.let {
                HotelDetailScreen(
                    hotel = it,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onBookRoomClick = { roomTypeId ->
                        if (currentUser != null) {
                            navController.navigate(BookingScreen(args.hotelId, roomTypeId))
                        } else {
                            navController.navigate(LoginScreen)
                        }
                    }
                )
            }
        }

        composable<BookingScreen> { backStackEntry ->
            // Authentication guard
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(LoginScreen) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                return@composable
            }
            
            val args = backStackEntry.toRoute<BookingScreen>()
            val hotel = hotelViewModel.getHotelById(args.hotelId)
            val roomType = hotel?.roomTypes?.find { it.id == args.roomTypeId }
            
            if (hotel != null && roomType != null) {
                BookingScreen(
                    hotel = hotel,
                    roomType = roomType,
                    checkInDate = bookingViewModel.uiState.checkInDate,
                    checkOutDate = bookingViewModel.uiState.checkOutDate,
                    guestCount = bookingViewModel.uiState.guestCount,
                    specialRequests = bookingViewModel.uiState.specialRequests,
                    onCheckInDateChange = { date ->
                        bookingViewModel.updateBookingDetails(checkInDate = date)
                    },
                    onCheckOutDateChange = { date ->
                        bookingViewModel.updateBookingDetails(checkOutDate = date)
                    },
                    onGuestCountChange = { count ->
                        bookingViewModel.updateBookingDetails(guestCount = count)
                    },
                    onSpecialRequestsChange = { requests ->
                        bookingViewModel.updateBookingDetails(specialRequests = requests)
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onContinueClick = {
                        navController.navigate(PaymentScreen(args.hotelId, args.roomTypeId))
                    },
                    isLoading = bookingViewModel.uiState.isLoading
                )
            }
        }

        composable<PaymentScreen> { backStackEntry ->
            // Authentication guard
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(LoginScreen) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                return@composable
            }
            
            val args = backStackEntry.toRoute<PaymentScreen>()
            val hotel = hotelViewModel.getHotelById(args.hotelId)
            val roomType = hotel?.roomTypes?.find { it.id == args.roomTypeId }
            
            if (hotel != null && roomType != null) {
                PaymentScreen(
                    hotel = hotel,
                    roomType = roomType,
                    checkInDate = bookingViewModel.uiState.checkInDate,
                    checkOutDate = bookingViewModel.uiState.checkOutDate,
                    guestCount = bookingViewModel.uiState.guestCount,
                    paymentMethods = bookingViewModel.uiState.paymentMethods,
                    selectedPaymentMethod = bookingViewModel.uiState.selectedPaymentMethod,
                    onPaymentMethodSelect = { paymentMethod ->
                        bookingViewModel.selectPaymentMethod(paymentMethod)
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onConfirmBookingClick = {
                        bookingViewModel.bookRoom(args.hotelId, args.roomTypeId)
                    },
                    isLoading = bookingViewModel.uiState.isLoading,
                    isBookingSuccess = bookingViewModel.uiState.isBookingSuccess,
                    errorMessage = bookingViewModel.uiState.errorMessage,
                    lastBooking = bookingViewModel.uiState.lastBooking,
                    onNavigateToHome = {
                        bookingViewModel.resetBookingSuccess()
                        bookingViewModel.resetBookingForm()
                        navController.navigate(HomeScreen) {
                            popUpTo(HomeScreen) { inclusive = true }
                        }
                    },
                    onNavigateToHistory = {
                        bookingViewModel.resetBookingSuccess()
                        bookingViewModel.resetBookingForm()
                        navController.navigate(BookingHistoryScreen)
                    }
                )
                
                // Clear error message after some time
                LaunchedEffect(bookingViewModel.uiState.errorMessage) {
                    if (bookingViewModel.uiState.errorMessage != null) {
                        kotlinx.coroutines.delay(5000) // Clear error after 5 seconds
                        bookingViewModel.clearError()
                    }
                }
            }
        }

        composable<BookingHistoryScreen> {
            // Authentication guard
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(LoginScreen) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                return@composable
            }
            
            BookingHistoryScreen(
                bookings = bookings,
                onBackClick = {
                    navController.popBackStack()
                },
                onCancelBooking = { bookingId ->
                    bookingViewModel.cancelBooking(bookingId)
                }
            )
        }

        composable<ProfileScreen> {
            // Authentication guard
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(LoginScreen) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                return@composable
            }
            
            ProfileScreen(
                user = currentUser,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditProfileClick = {
                    navController.navigate(EditProfileScreen)
                },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(LoginScreen) {
                        popUpTo(HomeScreen) { inclusive = true }
                    }
                }
            )
        }

        composable<EditProfileScreen> {
            // Authentication guard
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(LoginScreen) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                return@composable
            }
            
            EditProfileScreen(
                user = currentUser,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = { name, phoneNumber ->
                    profileViewModel.updateProfile(name, phoneNumber)
                },
                isLoading = profileViewModel.uiState.isLoading,
                errorMessage = profileViewModel.uiState.errorMessage
            )

            // Handle successful profile update
            LaunchedEffect(profileViewModel.uiState.isUpdateSuccess) {
                if (profileViewModel.uiState.isUpdateSuccess) {
                    profileViewModel.resetUpdateSuccess()
                    navController.popBackStack()
                }
            }
        }
    }
}
